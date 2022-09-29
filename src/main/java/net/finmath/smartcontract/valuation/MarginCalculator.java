package net.finmath.smartcontract.valuation;


import net.finmath.marketdata.products.Swap;
import net.finmath.marketdata.products.SwapLeg;
import net.finmath.modelling.DescribedProduct;
import net.finmath.modelling.ProductDescriptor;
import net.finmath.modelling.descriptor.InterestRateSwapLegProductDescriptor;
import net.finmath.modelling.descriptor.InterestRateSwapProductDescriptor;
import net.finmath.modelling.descriptor.xmlparser.FPMLParser;
import net.finmath.modelling.productfactory.InterestRateAnalyticProductFactory;
import net.finmath.smartcontract.model.ValuationResult;
import net.finmath.smartcontract.oracle.SmartDerivativeContractSettlementOracle;
import net.finmath.smartcontract.oracle.historical.ValuationOraclePlainSwapHistoricScenarios;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import net.finmath.smartcontract.simulation.scenariogeneration.IRMarketDataParser;
import net.finmath.smartcontract.simulation.scenariogeneration.IRMarketDataSet;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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


	public MarginCalculator() { }

	/**
	 * Calculates the margin between t_2 and t_1.
	 *
	 * @param marketDataStart Curve name at time t_1.
	 * @param marketDataEnd Curve name at time t_2.
	 * @param productData  Trade.
	 * @return the margin (Float).
	 * @throws Exception Exception
	 */
	public ValuationResult getValue(String marketDataStart, String marketDataEnd, String productData) throws Exception {
		SmartDerivativeContractDescriptor productDescriptor = SDCXMLParser.parse(productData);

		double value = calculateMarginFromString(marketDataStart, marketDataEnd, productDescriptor);
		String currency = "EUR";
		LocalDateTime valuationDate = LocalDateTime.now();

		return new ValuationResult().value(BigDecimal.valueOf(value)).currency(currency).valuationDate(valuationDate.toString());
	}

	/**
	 * Calculates the margin between t_2 and t_1.
	 *
	 * @param marketDataStart       Curve string at time t_1.
	 * @param marketDataEnd       Curve string at time t_2.
	 * @param productDescriptor
	 * @return A String containing t_2 (Date) and the margin (Float).
	 * @throws Exception Exception
	 */
	private double calculateMarginFromString(String marketDataStart, String marketDataEnd, SmartDerivativeContractDescriptor productDescriptor) throws Exception {

		List<IRMarketDataSet> marketDataSetsStart = IRMarketDataParser.getScenariosFromJsonString(marketDataStart);
		Validate.isTrue(marketDataSetsStart.size() == 1, "Parameter marketDataStart should be only a single market data set");

		List<IRMarketDataSet> marketDataSetsEnd = IRMarketDataParser.getScenariosFromJsonString(marketDataEnd);
		Validate.isTrue(marketDataSetsEnd.size() == 1, "Parameter marketDataStart should be only a single market data set");

		String ownerPartyID = productDescriptor.getUnderlyingReceiverPartyID();
		InterestRateSwapProductDescriptor underlying = (InterestRateSwapProductDescriptor)new FPMLParser(ownerPartyID, "forward-EUR-6M", "discount-EUR-OIS").getProductDescriptor(productDescriptor.getUnderlying());

		return calculateMargin(List.of(marketDataSetsStart.get(0), marketDataSetsEnd.get(0)), productDescriptor, underlying);

	}

	/**
	 * Calculates the margin for a list of market data scenarios.
	 *
	 * @param scenarioList list of market data scenarios.
	 * @param underlying
	 * @return A String containing the last date and the margin (Float).
	 * @throws Exception Exception
	 */
	private double calculateMargin(List<IRMarketDataSet> scenarioList, SmartDerivativeContractDescriptor productDescriptor, InterestRateSwapProductDescriptor underlying) throws Exception {

		LocalDate referenceDate = LocalDate.of(2022, 9, 5);
		InterestRateSwapLegProductDescriptor legReceiver = (InterestRateSwapLegProductDescriptor) underlying.getLegReceiver();
		InterestRateSwapLegProductDescriptor legPayer = (InterestRateSwapLegProductDescriptor) underlying.getLegPayer();
		InterestRateAnalyticProductFactory productFactory = new InterestRateAnalyticProductFactory(referenceDate);
		DescribedProduct<? extends ProductDescriptor> legReceiverProduct = productFactory.getProductFromDescriptor(legReceiver);
		DescribedProduct<? extends ProductDescriptor> legPayerProduct = productFactory.getProductFromDescriptor(legPayer);
		final List<LocalDateTime> scenarioDates = scenarioList.stream().map(scenario -> scenario.getDate()).sorted().collect(Collectors.toList());

		Swap swap = new Swap((SwapLeg) legReceiverProduct, (SwapLeg) legPayerProduct);

		final ValuationOraclePlainSwapHistoricScenarios oracle = new ValuationOraclePlainSwapHistoricScenarios(swap, 1.0, scenarioList);
		final SmartDerivativeContractSettlementOracle margin = new SmartDerivativeContractSettlementOracle(oracle);

		double valueWithCurves1 = 0.0;
		double valueWithCurves2 = 0.0;
		double marginCall = 0.0;

		//@Todo: Fix for intraday
		boolean isTradeStartToday = true;
		if (!isTradeStartToday) {
			valueWithCurves1 = oracle.getValue(scenarioDates.get(1), scenarioDates.get(1));
			valueWithCurves2 = oracle.getValue(scenarioDates.get(1), scenarioDates.get(0));
			marginCall = margin.getMargin(scenarioDates.get(0), scenarioDates.get(1)); // to remove
		} else {
			valueWithCurves1 = oracle.getValue(scenarioDates.get(1), scenarioDates.get(1));
			marginCall = valueWithCurves1;
		}

		return marginCall;

	}
}
