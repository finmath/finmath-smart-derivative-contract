package net.finmath.smartcontract.valuation.marketdata.generators;

import io.reactivex.rxjava3.core.Observable;

public interface MarketDataGeneratorInterface<T> {

	public Observable<T> asObservable();

}
