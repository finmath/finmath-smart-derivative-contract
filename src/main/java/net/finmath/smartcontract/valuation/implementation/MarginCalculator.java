package net.finmath.smartcontract.valuation.implementation;

import net.finmath.marketdata.products.Swap;
import net.finmath.marketdata.products.SwapLeg;
import net.finmath.modelling.DescribedProduct;
import net.finmath.modelling.ProductDescriptor;
import net.finmath.modelling.descriptor.InterestRateSwapLegProductDescriptor;
import net.finmath.modelling.descriptor.InterestRateSwapProductDescriptor;
import net.finmath.modelling.descriptor.xmlparser.FPMLParser;
import net.finmath.modelling.productfactory.InterestRateAnalyticProductFactory;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataset;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationParserDataItems;
import net.finmath.smartcontract.model.MarginResult;
import net.finmath.smartcontract.model.MarketDataSet;
import net.finmath.smartcontract.model.MarketDataSetValuesInner;
import net.finmath.smartcontract.model.ValueResult;
import net.finmath.smartcontract.valuation.oracle.SmartDerivativeContractSettlementOracle;
import net.finmath.smartcontract.valuation.oracle.interestrates.ValuationOraclePlainSwap;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.DoubleUnaryOperator;

/**
 * Calculation of the settlement using Smart Derivative Contract with an Swap contained in a FPML,
 * using a valuation oracle with historic market data.
 * For details see the corresponding white paper at SSRN.
 *
 * @author Christian Fries
 * @author Peter Kohl-Landgraf
 * @author Björn Paffen
 * @author Stefanie Weddigen
 * @author Dietmar Schnabel
 */
public class MarginCalculator {

	private static final Logger logger = LoggerFactory.getLogger(MarginCalculator.class);

	private final DoubleUnaryOperator rounding;

	public MarginCalculator(DoubleUnaryOperator rounding) {this.rounding = rounding;}

	public MarginCalculator() {
		this(x -> Math.round(x * 1000) / 1000.0);
	}

	/**
	 * Calculates the margin between t_2 and t_1.
	 *
	 * @param marketDataStart Curve name at time t_1.
	 * @param marketDataEnd   Curve name at time t_2.
	 * @param productData     Trade.
	 * @return the margin (MarginResult).
	 * @throws Exception Exception
	 */
	public MarginResult getValue(String marketDataStart, String marketDataEnd, String productData) throws Exception {
		SmartDerivativeContractDescriptor productDescriptor = SDCXMLParser.parse(productData);

		List<CalibrationDataset> marketDataSetsStart = CalibrationParserDataItems.getScenariosFromJsonString(marketDataStart);
		Validate.isTrue(marketDataSetsStart.size() == 1, "Parameter marketDataStart should be only a single market data set");

		List<CalibrationDataset> marketDataSetsEnd = CalibrationParserDataItems.getScenariosFromJsonString(marketDataEnd);
		Validate.isTrue(marketDataSetsEnd.size() == 1, "Parameter marketDataStart should be only a single market data set");

		String ownerPartyID = productDescriptor.getUnderlyingReceiverPartyID();
		InterestRateSwapProductDescriptor underlying = (InterestRateSwapProductDescriptor) new FPMLParser(ownerPartyID, "forward-EUR-6M", "discount-EUR-OIS").getProductDescriptor(productDescriptor.getUnderlying());

		LocalDateTime startDate = marketDataSetsStart.get(0).getDate();
		LocalDateTime endDate = marketDataSetsEnd.get(0).getDate();
		double value = calculateMargin(List.of(marketDataSetsStart.get(0), marketDataSetsEnd.get(0)), startDate, endDate, productDescriptor, underlying);

		String currency = "EUR";
		LocalDateTime valuationDate = LocalDateTime.now();

		return new MarginResult().value(BigDecimal.valueOf(rounding.applyAsDouble(value))).currency(currency).valuationDate(valuationDate.toString());
	}

	public MarginResult getValue(MarketDataSet marketDataStart, MarketDataSet marketDataEnd, String productData) throws Exception {
		SmartDerivativeContractDescriptor productDescriptor = SDCXMLParser.parse(productData);

		List<CalibrationDataItem.Spec> marketdataItemList = productDescriptor.getMarketdataItemList();
		Set<CalibrationDataItem> calibrationDataItemsStart = new HashSet<>();
		List<MarketDataSetValuesInner> marketDataValuesStart = marketDataStart.getValues();
		marketdataItemList.forEach(marketDataItemSpec -> marketDataValuesStart
				.stream()
				.filter(
						marketDataValue -> marketDataValue.getSymbol().equals(marketDataItemSpec.getKey())
				)
				.map(
						mdv -> new CalibrationDataItem(marketDataItemSpec, mdv.getValue(), mdv.getDataTimestamp().toLocalDateTime())
				)
				.forEach(calibrationDataItemsStart::add));

		List<CalibrationDataset> marketDataSetsStart = new ArrayList<>();
		marketDataSetsStart.add(new CalibrationDataset(calibrationDataItemsStart, marketDataStart.getRequestTimestamp().toLocalDateTime()));

		Set<CalibrationDataItem> calibrationDataItemsEnd = new HashSet<>();
		List<MarketDataSetValuesInner> marketDataValuesEnd = marketDataEnd.getValues();
		marketdataItemList.forEach(marketDataItemSpec -> marketDataValuesEnd
				.stream()
				.filter(
						marketDataValue -> marketDataValue.getSymbol().equals(marketDataItemSpec.getKey())
				)
				.map(
						mdv -> new CalibrationDataItem(marketDataItemSpec, mdv.getValue(), mdv.getDataTimestamp().toLocalDateTime())
				)
				.forEach(calibrationDataItemsEnd::add));

		List<CalibrationDataset> marketDataSetsEnd = new ArrayList<>();
		marketDataSetsEnd.add(new CalibrationDataset(calibrationDataItemsEnd, marketDataEnd.getRequestTimestamp().toLocalDateTime()));

		String ownerPartyID = productDescriptor.getUnderlyingReceiverPartyID();
		InterestRateSwapProductDescriptor underlying = (InterestRateSwapProductDescriptor) new FPMLParser(ownerPartyID, "forward-EUR-6M", "discount-EUR-OIS").getProductDescriptor(productDescriptor.getUnderlying());

		LocalDateTime startDate = marketDataSetsStart.get(0).getDate();
		LocalDateTime endDate = marketDataSetsEnd.get(0).getDate();
		double value = calculateMargin(List.of(marketDataSetsStart.get(0), marketDataSetsEnd.get(0)), startDate, endDate, productDescriptor, underlying);

		String currency = "EUR";
		LocalDateTime valuationDate = LocalDateTime.now();

		return new MarginResult().value(BigDecimal.valueOf(rounding.applyAsDouble(value))).currency(currency).valuationDate(valuationDate.toString());
	}

	public ValueResult getValue(String marketData, String productData) throws Exception {
		SmartDerivativeContractDescriptor productDescriptor = SDCXMLParser.parse(productData);

		List<CalibrationDataset> marketDataSets = CalibrationParserDataItems.getScenariosFromJsonString(marketData);
		Validate.isTrue(marketDataSets.size() == 1, "Parameter marketData should be only a single market data set");

		String ownerPartyID = productDescriptor.getUnderlyingReceiverPartyID();
		InterestRateSwapProductDescriptor underlying = (InterestRateSwapProductDescriptor) new FPMLParser(ownerPartyID, "forward-EUR-6M", "discount-EUR-OIS").getProductDescriptor(productDescriptor.getUnderlying());

		LocalDateTime endDate = marketDataSets.get(0).getDate();
		double value = calculateMargin(marketDataSets, null, endDate, productDescriptor, underlying);

		String currency = "EUR";
		LocalDateTime valuationDate = LocalDateTime.now();

		return new ValueResult().value(BigDecimal.valueOf(value)).currency(currency).valuationDate(valuationDate.toString());
	}

	public ValueResult getValue(MarketDataSet marketData, String productData) throws Exception {
		SmartDerivativeContractDescriptor productDescriptor = SDCXMLParser.parse(productData);

		Set<CalibrationDataItem> calibrationDataItems = new HashSet<>();

		List<CalibrationDataItem.Spec> marketdataItemList = productDescriptor.getMarketdataItemList();
		List<MarketDataSetValuesInner> marketDataValues = marketData.getValues();
		marketdataItemList.forEach(marketDataItemSpec -> marketDataValues
				.stream()
				.filter(
						marketDataValue -> marketDataValue.getSymbol().equals(marketDataItemSpec.getKey())
				)
				.map(
						mdv -> new CalibrationDataItem(marketDataItemSpec, mdv.getValue(), mdv.getDataTimestamp().toLocalDateTime())
				)
				.forEach(calibrationDataItems::add));

		List<CalibrationDataset> marketDataSets = new ArrayList<>();
		marketDataSets.add(new CalibrationDataset(calibrationDataItems, marketData.getRequestTimestamp().toLocalDateTime()));
		String ownerPartyID = productDescriptor.getUnderlyingReceiverPartyID();
		InterestRateSwapProductDescriptor underlying = (InterestRateSwapProductDescriptor) new FPMLParser(ownerPartyID, "forward-EUR-6M", "discount-EUR-OIS").getProductDescriptor(productDescriptor.getUnderlying());

		LocalDateTime endDate = marketDataSets.get(0).getDate();
		double value = calculateMargin(marketDataSets, null, endDate, productDescriptor, underlying);

		String currency = "EUR";

		return new ValueResult().value(BigDecimal.valueOf(value)).currency(currency).valuationDate(marketData.getRequestTimestamp().toLocalDateTime().toString());
	}

	/**
	 * Calculates the margin for a list of market data scenarios.
	 *
	 * @param marketDataSets    list of market data scenarios.
	 * @param productDescriptor The product descriptor (wrapper to the product XML)
	 * @param underlying        The underlying descriptor (wrapper to the underlying XML)
	 * @return The margin
	 * @throws Exception Exception
	 */
	private double calculateMargin(List<CalibrationDataset> marketDataSets, LocalDateTime startDate, LocalDateTime endState, SmartDerivativeContractDescriptor productDescriptor, InterestRateSwapProductDescriptor underlying) throws Exception {

		// Build product
		LocalDate referenceDate = productDescriptor.getTradeDate().toLocalDate();
		InterestRateSwapLegProductDescriptor legReceiver = (InterestRateSwapLegProductDescriptor) underlying.getLegReceiver();
		InterestRateSwapLegProductDescriptor legPayer = (InterestRateSwapLegProductDescriptor) underlying.getLegPayer();
		InterestRateAnalyticProductFactory productFactory = new InterestRateAnalyticProductFactory(referenceDate);
		DescribedProduct<? extends ProductDescriptor> legReceiverProduct = productFactory.getProductFromDescriptor(legReceiver);
		DescribedProduct<? extends ProductDescriptor> legPayerProduct = productFactory.getProductFromDescriptor(legPayer);

		Swap swap = new Swap((SwapLeg) legReceiverProduct, (SwapLeg) legPayerProduct);

		// Build valuation oracle with given market data.
		final ValuationOraclePlainSwap oracle = new ValuationOraclePlainSwap(swap, 1.0, marketDataSets);
		final SmartDerivativeContractSettlementOracle margin = new SmartDerivativeContractSettlementOracle(oracle);

		double marginCall = 0.0;

		if (Objects.isNull(startDate)) {
			marginCall = oracle.getValue(endState, endState);
		} else {
			marginCall = margin.getMargin(startDate, endState);
		}

		return marginCall;
	}
}
