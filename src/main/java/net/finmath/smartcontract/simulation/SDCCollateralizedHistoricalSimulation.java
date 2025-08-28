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

import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.products.AnalyticProduct;
import net.finmath.marketdata.products.Swap;
import net.finmath.marketdata.products.SwapLeg;
import net.finmath.modelling.DescribedProduct;
import net.finmath.modelling.ProductDescriptor;
import net.finmath.modelling.descriptor.InterestRateSwapLegProductDescriptor;
import net.finmath.modelling.descriptor.InterestRateSwapProductDescriptor;
import net.finmath.modelling.descriptor.xmlparser.FPMLParser;
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
	private static final String FORWARD_EUR_6M = "forward-EUR-6M";
	private static final String DISCOUNT_EUR_OIS = "discount-EUR-OIS";
	
	// TODO initialize LocalDate keys with marketData dates
	private static TreeMap<LocalDate, Double> marketValueMap = new TreeMap<>(); // V_i
	private static TreeMap<LocalDate, Double> marketValueChangeMap = new TreeMap<>(); // Y_i
	private static TreeMap<LocalDate, Double> cappedMarketValueChangeMap  = new TreeMap<>(); // X_i
	private static TreeMap<LocalDate, Double> collateralAccountMap = new TreeMap<>(); // C_i
	private static TreeMap<LocalDate, Double> gapAmountMap = new TreeMap<>(); // Z_i
	private static TreeMap<LocalDate, Double> gapAccountMap = new TreeMap<>(); //D_i
	
	private static double marginLimitLower = 0.0;
	private static double marginLimitUpper = 0.0;
	
	
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
		
		String ownerPartyID = productDescriptor.getUnderlyingReceiverPartyID();
		InterestRateSwapProductDescriptor underlying = (InterestRateSwapProductDescriptor) new FPMLParser(ownerPartyID, FORWARD_EUR_6M, DISCOUNT_EUR_OIS).getProductDescriptor(productDescriptor.getUnderlying());
		
		// Build product
		LocalDate referenceDate = productDescriptor.getTradeDate();
		InterestRateSwapLegProductDescriptor legReceiver = (InterestRateSwapLegProductDescriptor) underlying.getLegReceiver();
		InterestRateSwapLegProductDescriptor legPayer = (InterestRateSwapLegProductDescriptor) underlying.getLegPayer();
		InterestRateAnalyticProductFactory productFactory = new InterestRateAnalyticProductFactory(referenceDate);
		DescribedProduct<? extends ProductDescriptor> legReceiverProduct = productFactory.getProductFromDescriptor(legReceiver);
		DescribedProduct<? extends ProductDescriptor> legPayerProduct = productFactory.getProductFromDescriptor(legPayer);

		Swap swap = new Swap((SwapLeg) legReceiverProduct, (SwapLeg) legPayerProduct);
		
		// Float and fix leg have same payment dates
		// TODO get Payment dates/times of the swap
		
		// First entry of marketDataMap equals product referenceDate?
		TreeMap<LocalDate, CalibrationDataset> marketDataMap = null;
		for (Map.Entry<LocalDate, CalibrationDataset> marketDataCurrent : marketDataMap.entrySet()) {
		    Map.Entry<LocalDate, CalibrationDataset> marketDataPrevious = marketDataMap.lowerEntry(marketDataCurrent.getKey()); // direktes Vorgänger-Datum
		    if (marketDataPrevious != null) {
				AnalyticModel calibratedModelPrevious = getCalibratedModel(marketDataPrevious.getValue(), marketDataPrevious.getKey().atStartOfDay());		    	
				AnalyticModel calibratedModelCurrent = getCalibratedModel(marketDataCurrent.getValue(), marketDataCurrent.getKey().atStartOfDay());
				
				double valuePrevious = swap.getValue(0.0, calibratedModelPrevious); 
				double valueCurrent = swap.getValue(0.0, calibratedModelCurrent); 
				// Y-i = V(t_i) - V(t_{i-1})*(1+r_{i-1}*(t_i-t_{i-1}))
				double marketValueChange = valueCurrent - valuePrevious; 
				// X_i
				double cappedMarketValueChange = Math.min(Math.max(marketValueChange, -marginLimitLower), marginLimitUpper); // X_i
				
				double dt = FloatingpointDate.getFloatingPointDateFromDate(marketDataPrevious.getKey(),  marketDataCurrent.getKey());
				double accrualRate = 1 / calibratedModelPrevious.getDiscountCurve(DISCOUNT_EUR_OIS).getDiscountFactor(dt);
				// C_i = C_{i-1}*(1+r_{i-1}*(t_i-t_{i-1})) + X_i
				double collateralAccount = collateralAccountMap.get(marketDataPrevious.getKey()) * accrualRate + cappedMarketValueChange;
				// Z_i = X_i - Y_i 
				double gapAmount = cappedMarketValueChange - marketValueChange;
				// D_i = D_{i-1}*(1+r_{i-1}*(t_i-t_{i-1})) + Z_i
				double gapAccount = gapAccountMap.get(marketDataPrevious.getKey()) * accrualRate + gapAmount;
				
				// TODO handling of value on paymentDate inclusive / exclusive

		    }
		
		}
		
	}

	
	private static AnalyticModel getCalibratedModel(CalibrationDataset calibrationDataset, LocalDateTime marketDataTime) {
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
	

}
