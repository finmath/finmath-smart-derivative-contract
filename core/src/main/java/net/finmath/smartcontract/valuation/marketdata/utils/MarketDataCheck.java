package net.finmath.smartcontract.valuation.marketdata.utils;

import net.finmath.smartcontract.model.MarketDataList;
import net.finmath.smartcontract.product.xml.Smartderivativecontract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MarketDataCheck {

	private static final Logger logger = LoggerFactory.getLogger(MarketDataCheck.class);

	private MarketDataCheck(){}

	public static MarketDataErrors checkMarketData(MarketDataList marketDataList, Smartderivativecontract sdc) {
		int counter = 0;

		// check if no data provided
		if (marketDataList.getPoints().equals(new MarketDataList().getPoints())) {
			logger.error("marketData: {}", marketDataList);
			logger.error("trade will be suspended, no settlement will be performed");
			MarketDataErrors errors = new MarketDataErrors(true);
			errors.setErrorMessage("error in marketData service - no data generated");
			errors.addMissingData("all, no data provided");
			return errors;
		}

		// check missing data points
		boolean hasAllIDs = true;
		MarketDataErrors errors = new MarketDataErrors(true);
		for (Smartderivativecontract.Settlement.Marketdata.Marketdataitems.Item spec : sdc.getSettlement().getMarketdata().getMarketdataitems().getItem()) {

			String id = spec.getSymbol().get(0);
			boolean present = marketDataList.getPoints().stream().anyMatch(marketDataPoint -> marketDataPoint.getId().equals(id));
			if (!present) {
				hasAllIDs = false;
				errors.addMissingData(id);
				logger.error("marketData invalid - marketDataPoint with id '{}' is missing", id);
				counter++;
			}
		}

		if (!hasAllIDs) {
			String reason = "error in marketData service - missing points in marketData";
			logger.error(reason);
			logger.error("'{}' marketDataItems are missing", counter);
			errors.setErrorMessage(reason);
			return errors;
		}

		//TODO add checks for
		// 	data distortion		=> return false
		// add suspended because invalid data
		logger.info("provided marketData is fine compared to the product data xml");
		return new MarketDataErrors(false);
	}
}
