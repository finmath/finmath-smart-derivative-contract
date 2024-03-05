package net.finmath.smartcontract.valuation.marketdata.adapters;

import io.reactivex.rxjava3.core.Observable;

public interface MarketDataStreamInterface<T> {

	public Observable<T> asObservable();

	public void closeStream();
}
