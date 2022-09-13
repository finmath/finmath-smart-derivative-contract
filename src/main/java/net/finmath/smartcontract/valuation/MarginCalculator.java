
package net.finmath.smartcontract.valuation;



import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import net.finmath.marketdata.products.Swap;
import net.finmath.marketdata.products.SwapLeg;
import net.finmath.modelling.DescribedProduct;
import net.finmath.modelling.ProductDescriptor;
import net.finmath.modelling.descriptor.InterestRateSwapLegProductDescriptor;
import net.finmath.modelling.descriptor.InterestRateSwapProductDescriptor;
import net.finmath.modelling.productfactory.InterestRateAnalyticProductFactory;
import net.finmath.smartcontract.descriptor.TradeDescriptor;
import net.finmath.smartcontract.descriptor.xmlparser.FPMLParser;
import net.finmath.smartcontract.oracle.SmartDerivativeContractSettlementOracle;
import net.finmath.smartcontract.oracle.historical.ValuationOraclePlainSwapHistoricScenarios;

import net.finmath.smartcontract.simulation.scenariogeneration.IRMarketDataScenario;
import net.finmath.smartcontract.util.SDCConstants;
import net.finmath.smartcontract.util.SDCDateUtil;
import net.finmath.smartcontract.util.SDCXMLUtil;
import net.finmath.smartcontract.valuation.scenariogeneration.IRScenarioGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

	private  FPMLParser parser;
	private  InterestRateSwapProductDescriptor productDescriptor;
	private  ContractValuation contractValuation;
	private  boolean isTradeStartToday; 

	private static final DateTimeFormatter providedDateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
	
	
	
	public MarginCalculator() {
		
		
	}

	
	/**
	 * Calculates the margin between t_2 and t_1.
	 *
	 * @param jsonFileName1 Curve name at time t_1.
	 * @param jsonFileName2 Curve name at time t_2.
	 * @param fpmlFile      Trade.
	 * @return the margin (Float).
	 * @throws Exception Exception
	 */
    public double getValue (String jsonFileName1, String jsonFileName2, File fpmlFile) throws Exception {
    	parser = new FPMLParser("party1", "discount-EUR-OIS","forward-EUR-6M", fpmlFile);
    	return calculateMarginFromFile(jsonFileName1, jsonFileName2);
    }
    
    /**
	 * Calculates the margin between t_2 and t_1.
	 *
	 * @param jsonString1 Curve name at time t_1.
	 * @param jsonString2 Curve name at time t_2.
	 * @param fpmlString      Trade.
	 * @return the margin (Float).
	 * @throws Exception Exception
	 */
    public double getValue (String jsonString1, String jsonString2, String fpmlString) throws Exception {
    	parser = new FPMLParser("party1", "discount-EUR-OIS","forward-EUR-6M", fpmlString);
    	isTradeStartToday = validateStartDate(fpmlString);
    	
    	return calculateMarginFromString(jsonString1, jsonString2);
    }
    /**
	 * @return the ContractValuation.
	 */
    public ContractValuation getContractValuation() {
    	return contractValuation;
    }
    /**
	 * @return the ContractValuation as JSON.
	 */
    public String getContractValuationAsJSON() {
    	
    	Gson gson = new Gson();
    	String json = gson.toJson(contractValuation);
    	return json;
    }
	/**
	 * Calculates the margin between t_2 and t_1.
	 *
	 * @param jsonFileName1 Curve name at time t_1.
	 * @param jsonFileName2 Curve name at time t_2.
	 * @return the margin (Float).
	 * @throws Exception Exception
	 */
	private double calculateMarginFromFile(String jsonFileName1, String jsonFileName2) throws Exception {
		
		productDescriptor = (InterestRateSwapProductDescriptor) parser.getProductDescriptor();
		
		final LocalDate startDate = parser.getStartDate().plusDays(-1);
		final LocalDate maturity = parser.getMaturityDate().plusDays(1);
		
		// Generate the scenario list
		
		List<IRMarketDataScenario> scenarioList = IRScenarioGenerator.getScenariosFromJsonFile(jsonFileName1,providedDateFormat).stream().filter(S->S.getDate().toLocalDate().isAfter(startDate)).filter(S->S.getDate().toLocalDate().isBefore(maturity)).collect(Collectors.toList());
		List<IRMarketDataScenario> scenarioList2 = IRScenarioGenerator.getScenariosFromJsonFile(jsonFileName2,providedDateFormat).stream().filter(S->S.getDate().toLocalDate().isAfter(startDate)).filter(S->S.getDate().toLocalDate().isBefore(maturity)).collect(Collectors.toList());
		scenarioList.addAll(scenarioList2);
		
		return calculateMargin( scenarioList);
		
	}	
	
	/**
	 * Calculates the margin between t_2 and t_1.
	 *
	 * @param jsonString1 Curve string at time t_1.
	 * @param jsonString2 Curve string at time t_2.
	 * @return A String containing t_2 (Date) and the margin (Float).
	 * @throws Exception Exception
	 */
	private  double calculateMarginFromString(String jsonString1, String jsonString2) throws Exception {
		
		productDescriptor = (InterestRateSwapProductDescriptor) parser.getProductDescriptor();
		
		final LocalDate startDate = parser.getStartDate().plusDays(-1);
		final LocalDate maturity = parser.getMaturityDate().plusDays(1);
		
		
		
		// Generate the scenario list
		
//		List<IRMarketDataScenario> scenarioList = IRScenarioGenerator.getScenariosFromJsonString(jsonString1,providedDateFormat).stream().filter(S->S.getDate().toLocalDate().isAfter(startDate)).filter(S->S.getDate().toLocalDate().isBefore(maturity)).collect(Collectors.toList());
//		List<IRMarketDataScenario> scenarioList2 = IRScenarioGenerator.getScenariosFromJsonString(jsonString2,providedDateFormat).stream().filter(S->S.getDate().toLocalDate().isAfter(startDate)).filter(S->S.getDate().toLocalDate().isBefore(maturity)).collect(Collectors.toList());
		List<IRMarketDataScenario> scenarioList = IRScenarioGenerator.getScenariosFromJsonString(jsonString1,providedDateFormat).stream().filter(S->S.getDate().toLocalDate().isBefore(maturity)).collect(Collectors.toList());
		List<IRMarketDataScenario> scenarioList2 = IRScenarioGenerator.getScenariosFromJsonString(jsonString2,providedDateFormat).stream().filter(S->S.getDate().toLocalDate().isBefore(maturity)).collect(Collectors.toList());		
		
		scenarioList.addAll(scenarioList2);
		
		return calculateMargin( scenarioList);
		
	}
	
	/**
	 * Calculates the margin for a list of market data scenarios.
	 *
	 * @param scenarioList list of market data scenarios.
	 * @return A String containing the last date and the margin (Float).
	 * @throws Exception Exception
	 */
	private  double calculateMargin(List<IRMarketDataScenario> scenarioList) throws Exception {
		
		
		TradeDescriptor tradeDescriptor = parser.getTradeDescriptor();
		
		LocalDate referenceDate = tradeDescriptor.getTradeDate();
		InterestRateSwapLegProductDescriptor legReceiver	= (InterestRateSwapLegProductDescriptor) productDescriptor.getLegReceiver();
		InterestRateSwapLegProductDescriptor legPayer		= (InterestRateSwapLegProductDescriptor) productDescriptor.getLegPayer();
		InterestRateAnalyticProductFactory productFactory = new InterestRateAnalyticProductFactory(referenceDate);
		DescribedProduct<? extends ProductDescriptor> legReceiverProduct = productFactory.getProductFromDescriptor(legReceiver);
		DescribedProduct<? extends ProductDescriptor> legPayerProduct = productFactory.getProductFromDescriptor(legPayer);

		Swap swap = new Swap((SwapLeg)legReceiverProduct, (SwapLeg)legPayerProduct);
		

		double notional=parser.getNotional();
		final ValuationOraclePlainSwapHistoricScenarios oracle = new ValuationOraclePlainSwapHistoricScenarios(swap,notional,scenarioList);
		final SmartDerivativeContractSettlementOracle margin = new SmartDerivativeContractSettlementOracle(oracle);

		final List<LocalDateTime> scenarioDates = scenarioList.stream().map(scenario->scenario.getDate()).sorted().collect(Collectors.toList());

		
		double valueWithCurves1 = 0.0;
		double valueWithCurves2 = 0.0;
		double marginCall = 0.0;
		
		if (!isTradeStartToday) {
			valueWithCurves1 = oracle.getValue(scenarioDates.get(1), scenarioDates.get(1));
			valueWithCurves2 = oracle.getValue(scenarioDates.get(1), scenarioDates.get(0));
			marginCall 		 = margin.getMargin(scenarioDates.get(0), scenarioDates.get(1)); // to remove
		} else {
			valueWithCurves1 = oracle.getValue(scenarioDates.get(1), scenarioDates.get(1));
			marginCall = valueWithCurves1;
		}
		
//		valueWithCurves1 = oracle.getValue(scenarioDates.get(1), scenarioDates.get(1));
//		valueWithCurves2 = oracle.getValue(scenarioDates.get(1), scenarioDates.get(0));
//		marginCall 		 = margin.getMargin(scenarioDates.get(0), scenarioDates.get(1)); 	// to remove
		//String result = "\t" + DateTimeFormatter.ofPattern("dd.MM.yyyy").format(scenarioDates.get(1)).toString() + "\t" + String.valueOf(marginCall);
		

		contractValuation = new ContractValuation(LocalDateTime.now() , tradeDescriptor.getLegalEntitiesExternalReferences(), tradeDescriptor.getLegalEntitiesNames(), valueWithCurves1, valueWithCurves2, marginCall);
//		contractValuation = new ContractValuation(scenarioDates.get(1), tradeDescriptor.getLegalEntitiesExternalReferences(), tradeDescriptor.getLegalEntitiesNames(), valueWithCurves1, valueWithCurves2, marginCall);
		contractValuation.setStartDate(parser.getStartDate().format(DateTimeFormatter.ofPattern(SDCConstants.DATE_FORMAT_yyyyMMdd)));
		contractValuation.setMaturityDate(parser.getMaturityDate().format(DateTimeFormatter.ofPattern(SDCConstants.DATE_FORMAT_yyyyMMdd)));
		contractValuation.setLegReceiver(tradeDescriptor.getLegReceiver());
		
		return marginCall;
		
	}



	public boolean validateStartDate(String fpml) {

//		String startDate = SDCXMLUtil.getXMLElement(fpml, SDCConstants.XPATH_FPML_SWAP_LEG1 + SDCConstants.XPATH_FPML_SWAP_START_DATE );
		String startDate = SDCXMLUtil.getXMLElement(fpml, SDCConstants.XPATH_FPML_TRADE_DATE);
		logger.info("Trade date = " + startDate);
		LocalDate date = SDCDateUtil.getDateFromString(startDate, SDCConstants.DATE_FORMAT_yyyy_MM_dd);
		LocalDate now = LocalDate.now();

//        if(date.isAfter(now)) {
////        if(date.isBefore(before) || date.equals(before)) {
//        	logger.info("Startdate of the trade is after: " + now + ", trade will not be valued!");
//        	return false;
//        }

		if(date.equals(now)){
			logger.info("Trade date of the trade is: " + now + ".  The trade will  be valued with T - 0!");
			return true;
		}
		logger.info("Trade date of the trade is  " + date + ".  The trade will  be valued with T and T-1!");
		return false;
	}


}
