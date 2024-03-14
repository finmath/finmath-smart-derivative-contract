package net.finmath.smartcontract.valuation.marketdata.generators;

import io.reactivex.rxjava3.core.Observable;
import net.finmath.smartcontract.valuation.marketdata.data.MarketDataList;

import java.util.List;

public class MarketDataGeneratorScenarioList implements MarketDataGeneratorInterface<MarketDataList> {

	public MarketDataGeneratorScenarioList(List<MarketDataList> scenarioList){

	}

	@Override
	public Observable<MarketDataList> asObservable() {
		return null;
	}
}
