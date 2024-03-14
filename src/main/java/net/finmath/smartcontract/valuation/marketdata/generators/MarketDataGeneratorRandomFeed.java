package net.finmath.smartcontract.valuation.marketdata.generators;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataset;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationParserDataItems;
import net.finmath.smartcontract.valuation.marketdata.data.MarketDataList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MarketDataGeneratorRandomFeed implements MarketDataGeneratorInterface<MarketDataList> {

	LocalDateTime endTime;
	CalibrationDataset referenceSet;

	private static final Logger logger = LoggerFactory.getLogger(MarketDataGeneratorRandomFeed.class);

	int simulationFrequencySec;

	public MarketDataGeneratorRandomFeed(Period processingPeriod, String referenceMarketDataStr, List<CalibrationDataItem.Spec> mdSpecs) throws Exception {
		this.endTime = LocalDateTime.now().plus(processingPeriod);
		referenceSet = CalibrationParserDataItems.getCalibrationDataSetFromXML(referenceMarketDataStr,mdSpecs);
		simulationFrequencySec = 3;
	}


	public Observable<MarketDataList> asObservable() {
		ObservableOnSubscribe<MarketDataList> observable = emitter -> {
			while (LocalDateTime.now().isBefore(endTime)) {
				CalibrationDataset shiftedScenario = getShiftedReferenceSet();
				emitter.onNext(shiftedScenario.toMarketDataList());
			}
			emitter.onComplete();
		};
		Period period;
		//   Observable.interval(0,period.get(TimeUnit.SECONDS.toChronoUnit()),TimeUnit.SECONDS).map(i->...)
		return Observable.create(observable).delay(this.simulationFrequencySec, TimeUnit.SECONDS);

		// https://betterprogramming.pub/rxjava-different-ways-of-creating-observables-7ec3204f1e23
	}


	private CalibrationDataset getShiftedReferenceSet() {
		double randomShiftBp = ThreadLocalRandom.current().nextDouble(-1, 1) / 10000;
		Set<CalibrationDataItem> shifted = this.referenceSet.getDataPoints().stream().map(datapoint -> datapoint.getClonedShifted(1 + randomShiftBp)).collect(Collectors.toSet());
		CalibrationDataset set = new CalibrationDataset(shifted, this.referenceSet.getDate());
		return set;
	}

}
