package net.finmath.smartcontract.simulation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.products.AnalyticProduct;
import net.finmath.marketdata.products.Swap;
import net.finmath.marketdata.products.SwapLeg;
import net.finmath.modelling.DescribedProduct;
import net.finmath.modelling.ProductDescriptor;
import net.finmath.modelling.descriptor.InterestRateSwapLegProductDescriptor;
import net.finmath.modelling.productfactory.InterestRateAnalyticProductFactory;
import net.finmath.smartcontract.model.ExceptionId;
import net.finmath.smartcontract.model.SDCException;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import net.finmath.smartcontract.valuation.client.ValuationClient;
import net.finmath.smartcontract.valuation.implementation.ParserConfigurationException;
import net.finmath.smartcontract.valuation.implementation.SAXException;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationContextImpl;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataset;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationParserDataItems;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationResult;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationSpecProvider;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.Calibrator;
import net.finmath.time.FloatingpointDate;

public class SDCCollateralizedHistoricalSimulation {
	
	private static final String FIXING = "Fixing";
	private static final String DEPOSIT = "Deposit";
	
	private TreeMap<LocalDateTime, Double> marketValueChange = new TreeMap<>(); // Y_i
	private TreeMap<LocalDateTime, Double> cappedMarketValueChange  = new TreeMap<>(); // X_i
	private TreeMap<LocalDateTime, Double> collateralAccount = new TreeMap<>(); // C_i
	private TreeMap<LocalDateTime, Double> gapAmount = new TreeMap<>(); // Z_i
	private TreeMap<LocalDateTime, Double> gapAccount = new TreeMap<>(); //D_i
	
	private double marginLimitLower;
	private double marginLimitUpper;
	
	
	public static void main(String args[]) throws Exception {
		
		final String product = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract.xml").readAllBytes(), StandardCharsets.UTF_8);
		SmartDerivativeContractDescriptor productDescriptor = null;
		try {
			productDescriptor = SDCXMLParser.parse(product);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}
		
		// Build product
		LocalDate referenceDate = productDescriptor.getTradeDate();
		InterestRateSwapLegProductDescriptor legReceiver = (InterestRateSwapLegProductDescriptor) underlying.getLegReceiver();
		InterestRateSwapLegProductDescriptor legPayer = (InterestRateSwapLegProductDescriptor) underlying.getLegPayer();
		InterestRateAnalyticProductFactory productFactory = new InterestRateAnalyticProductFactory(referenceDate);
		DescribedProduct<? extends ProductDescriptor> legReceiverProduct = productFactory.getProductFromDescriptor(legReceiver);
		DescribedProduct<? extends ProductDescriptor> legPayerProduct = productFactory.getProductFromDescriptor(legPayer);

		// Swap will be calculated outside
		Swap swap = new Swap((SwapLeg) legReceiverProduct, (SwapLeg) legPayerProduct);
		
		
		// Loop over list of daily marketData for valuation
		TreeMap<LocalDateTime, CalibrationDataset> scenarioList = null;
		for (Map.Entry<LocalDateTime, CalibrationDataset> valuationDate : scenarioList.entrySet()) {
			
		
		}
		
	}


	private double getValue(AnalyticProduct product, AnalyticModel current, AnalyticModel previous) {
		double valueCurrent = product.getValue(0, current);
		double valuePrevious = product.getValue(0, previous);

		// Y_i = V(t_i) - V(t_{i-1})*(1+r_{i-1}*(t_i-t_{i-1})
		double marketValueChange = valueCurrent - valuePrevious;

		// X_i = min(max(Y_i, -M^-), M^+)
		double cappedFloorMarketValueChange = Math.min(Math.max(marketValueChange, -10), 10);

		// Z_i = X_i - Y_i
		double gapAmount = cappedFloorMarketValueChange - marketValueChange;

		// need product schedule to handle / know about coupon payment dates

		return 0;
	}
	
	private AnalyticModel getCalibratedModel(CalibrationDataset calibrationDataset, LocalDateTime marketDataTime) {
		final CalibrationParserDataItems parser = new CalibrationParserDataItems();
		try {
			final Stream<CalibrationSpecProvider> calibrationItems = calibrationDataset.getDataAsCalibrationDataPointStream(parser);
			List<CalibrationDataItem> fixings = calibrationDataset.getDataPoints().stream().filter(
					cdi -> cdi.getSpec().getProductName().equals(FIXING) || cdi.getSpec().getProductName().equals(DEPOSIT)).toList();

			Calibrator calibrator = new Calibrator(fixings, new CalibrationContextImpl(marketDataTime, 1E-9));
			final Optional<CalibrationResult> optionalCalibrationResult = calibrator.calibrateModel(calibrationItems, new CalibrationContextImpl(marketDataTime, 1E-9));

			AnalyticModel calibratedModel = optionalCalibrationResult.orElseThrow().getCalibratedModel();
			return calibratedModel;
		} catch (final Exception e) {
			throw new SDCException(ExceptionId.SDC_CALIBRATION_ERROR, e.getMessage());
		}
	}
	
	// Search for the nearest ESTR fixing and add it to the calibration items as 1-day discount rate proxy
	private void addOvernightDepositRate(CalibrationDataset calibrationDataset) {
		List<CalibrationDataItem> estrFixings = calibrationDataset.getFixingDataItems().stream().filter(fixingItem ->
				fixingItem.getSpec().getCurveName().equals("ESTR") &&
						fixingItem.getSpec().getMaturity().equals("1D")).toList();
		if (!estrFixings.isEmpty()) {
			CalibrationDataItem nearestFixing = estrFixings.stream()
					.min((item1, item2) -> {
						double diff1 = FloatingpointDate.getFloatingPointDateFromDate(calibrationDataset.getDate(), item1.getDateTime());
						double diff2 = FloatingpointDate.getFloatingPointDateFromDate(calibrationDataset.getDate(), item2.getDateTime());
						return Double.compare(Math.abs(diff1), Math.abs(diff2));
					})
					.orElse(null);
			if (nearestFixing != null) {
				CalibrationDataItem.Spec calibrationItemSpecON = new CalibrationDataItem.Spec("EUREST1D", "ESTR","Overnight-Rate","1D");
				CalibrationDataItem calibrationItemON = new CalibrationDataItem(calibrationItemSpecON, nearestFixing.getQuote(), nearestFixing.getDateTime());
				calibrationDataset.getCalibrationDataItems().add(calibrationItemON);
			}
		}
	}

}
