package net.finmath.smartcontract.model;

import net.finmath.smartcontract.valuation.marketdata.data.MarketDataPoint;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MarketDataListTest {

	@Test
	void setPoints() {
		MarketDataList marketDataList = new MarketDataList();

		List<MarketDataPoint> points = new ArrayList<>();
		points.add(new MarketDataPoint());

		marketDataList.setPoints(points);

		assertNotNull(marketDataList);
		assertNotNull(marketDataList.toString());
		assertNotNull(marketDataList.getPoints());
	}
}