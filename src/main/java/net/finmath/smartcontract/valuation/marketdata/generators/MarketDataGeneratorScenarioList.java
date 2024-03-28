package net.finmath.smartcontract.valuation.marketdata.generators;


import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import net.finmath.smartcontract.model.MarketDataList;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("java:S125")
@Profile(value = {"dev", "test", "int", "default"})
@Service
public class MarketDataGeneratorScenarioList implements MarketDataGeneratorInterface<MarketDataList> {

	private static final Logger logger = LoggerFactory.getLogger(MarketDataGeneratorScenarioList.class);
	private Observable<MarketDataList> publishSubject;
	private int counter = 0;
	private final List<String> files = List.of("marketdata_2008-05-02.xml", "marketdata_2008-05-05.xml", "marketdata_2008-05-06.xml", "marketdata_2008-05-07.xml", "marketdata_2008-05-08.xml", "marketdata_2008-05-09.xml", "marketdata_2008-05-12.xml", "marketdata_2008-05-13.xml", "marketdata_2008-05-14.xml", "marketdata_2008-05-15.xml", "marketdata_2008-05-16.xml", "marketdata_2008-05-19.xml", "marketdata_2008-05-20.xml", "marketdata_2008-05-21.xml", "marketdata_2008-05-22.xml", "marketdata_2008-05-23.xml", "marketdata_2008-05-26.xml", "marketdata_2008-05-27.xml", "marketdata_2008-05-28.xml", "marketdata_2008-05-29.xml", "marketdata_2008-05-30.xml",
		"marketdata_2008-06-02.xml", "marketdata_2008-06-03.xml", "marketdata_2008-06-04.xml", "marketdata_2008-06-05.xml", "marketdata_2008-06-06.xml", "marketdata_2008-06-09.xml", "marketdata_2008-06-10.xml", "marketdata_2008-06-11.xml", "marketdata_2008-06-12.xml", "marketdata_2008-06-13.xml", "marketdata_2008-06-16.xml", "marketdata_2008-06-17.xml", "marketdata_2008-06-18.xml", "marketdata_2008-06-19.xml", "marketdata_2008-06-20.xml", "marketdata_2008-06-23.xml", "marketdata_2008-06-24.xml", "marketdata_2008-06-25.xml", "marketdata_2008-06-26.xml", "marketdata_2008-06-27.xml", "marketdata_2008-06-30.xml",
		"marketdata_2008-07-01.xml", "marketdata_2008-07-02.xml", "marketdata_2008-07-03.xml", "marketdata_2008-07-04.xml", "marketdata_2008-07-07.xml", "marketdata_2008-07-08.xml", "marketdata_2008-07-09.xml", "marketdata_2008-07-10.xml", "marketdata_2008-07-11.xml", "marketdata_2008-07-14.xml", "marketdata_2008-07-15.xml", "marketdata_2008-07-16.xml", "marketdata_2008-07-17.xml", "marketdata_2008-07-18.xml", "marketdata_2008-07-21.xml", "marketdata_2008-07-22.xml", "marketdata_2008-07-23.xml", "marketdata_2008-07-24.xml", "marketdata_2008-07-25.xml", "marketdata_2008-07-28.xml", "marketdata_2008-07-29.xml", "marketdata_2008-07-30.xml", "marketdata_2008-07-31.xml",
		"marketdata_2008-08-01.xml", "marketdata_2008-08-04.xml", "marketdata_2008-08-05.xml", "marketdata_2008-08-06.xml", "marketdata_2008-08-07.xml", "marketdata_2008-08-08.xml", "marketdata_2008-08-11.xml", "marketdata_2008-08-12.xml", "marketdata_2008-08-13.xml", "marketdata_2008-08-14.xml", "marketdata_2008-08-15.xml", "marketdata_2008-08-18.xml", "marketdata_2008-08-19.xml", "marketdata_2008-08-20.xml", "marketdata_2008-08-21.xml", "marketdata_2008-08-22.xml", "marketdata_2008-08-25.xml", "marketdata_2008-08-26.xml", "marketdata_2008-08-27.xml", "marketdata_2008-08-28.xml", "marketdata_2008-08-29.xml",
		"marketdata_2008-09-01.xml", "marketdata_2008-09-02.xml", "marketdata_2008-09-03.xml", "marketdata_2008-09-04.xml", "marketdata_2008-09-05.xml", "marketdata_2008-09-08.xml", "marketdata_2008-09-09.xml", "marketdata_2008-09-10.xml", "marketdata_2008-09-11.xml", "marketdata_2008-09-12.xml", "marketdata_2008-09-15.xml", "marketdata_2008-09-16.xml", "marketdata_2008-09-17.xml", "marketdata_2008-09-18.xml", "marketdata_2008-09-19.xml", "marketdata_2008-09-22.xml", "marketdata_2008-09-23.xml", "marketdata_2008-09-24.xml", "marketdata_2008-09-25.xml", "marketdata_2008-09-26.xml", "marketdata_2008-09-29.xml", "marketdata_2008-09-30.xml",
		"marketdata_2008-10-01.xml", "marketdata_2008-10-02.xml", "marketdata_2008-10-03.xml", "marketdata_2008-10-06.xml", "marketdata_2008-10-07.xml", "marketdata_2008-10-08.xml", "marketdata_2008-10-09.xml", "marketdata_2008-10-10.xml", "marketdata_2008-10-13.xml", "marketdata_2008-10-14.xml", "marketdata_2008-10-15.xml", "marketdata_2008-10-16.xml", "marketdata_2008-10-17.xml", "marketdata_2008-10-20.xml", "marketdata_2008-10-21.xml", "marketdata_2008-10-22.xml", "marketdata_2008-10-23.xml", "marketdata_2008-10-24.xml", "marketdata_2008-10-27.xml", "marketdata_2008-10-28.xml", "marketdata_2008-10-29.xml", "marketdata_2008-10-30.xml", "marketdata_2008-10-31.xml");


    /*public MarketDataGeneratorScenarioList(List<MarketDataList> scenarioList) {
        publishSubject = PublishSubject.create();
    }*/

	public MarketDataGeneratorScenarioList() {
		publishSubject = PublishSubject.create();
	}

	@Override
	public Observable<MarketDataList> asObservable() {
		publishSubject = Observable.create(emitter -> {
			emitter.onNext(getMarketDataCurve());
			emitter.onComplete();
		});

		return this.publishSubject;
	}

	private MarketDataList getMarketDataCurve() {
		if (counter >= files.size()) counter = 0;
		final String fileName = "net/finmath/smartcontract/valuation/historicalMarketData/" + files.get(counter);

		logger.info("counter: {}, file to retrieve: {}", counter, fileName);

		String marketDataXml;
		try {
			marketDataXml = new String(Objects.requireNonNull(MarketDataGeneratorScenarioList.class.getClassLoader().getResourceAsStream(fileName)).readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		counter++;
		return SDCXMLParser.unmarshalXml(marketDataXml, MarketDataList.class);
	}


}
