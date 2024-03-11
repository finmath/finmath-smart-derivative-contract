package net.finmath.smartcontract.valuation.marketdata.adapters;

import com.neovisionaries.ws.client.WebSocket;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataset;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationParserDataItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MarketDataRandomFeedAdapter extends LiveFeedAdapter<CalibrationDataset> {

	LocalDateTime endTime;
	CalibrationDataset referenceSet;

	private static final Logger logger = LoggerFactory.getLogger(MarketDataRandomFeedAdapter.class);

	int simulationFrequencySec;

	public MarketDataRandomFeedAdapter(Period processingPeriod, String referenceMarketDataJson) throws Exception {
		this.endTime = LocalDateTime.now().plus(processingPeriod);
		referenceSet = CalibrationParserDataItems.getScenariosFromJsonString(referenceMarketDataJson).get(0);
		simulationFrequencySec = 3;
	}


	public Observable<CalibrationDataset> asObservable() {
		ObservableOnSubscribe<CalibrationDataset> observable = emitter -> {
			while (LocalDateTime.now().isBefore(endTime)) {
				CalibrationDataset shiftedScenario = getShiftedReferenceSet();
				emitter.onNext(shiftedScenario);
			}
			emitter.onComplete();
		};
		Period period;
		//   Observable.interval(0,period.get(TimeUnit.SECONDS.toChronoUnit()),TimeUnit.SECONDS).map(i->...)
		return Observable.create(observable).delay(this.simulationFrequencySec, TimeUnit.SECONDS);

		// https://betterprogramming.pub/rxjava-different-ways-of-creating-observables-7ec3204f1e23
	}

    /*public void start() throws InterruptedException{
        // WE have two market data sets, just emit both of them on a frequent base
        while (LocalDateTime.now().isBefore(endTime)){

            IRMarketDataSet shiftedScenario = getShiftedReferenceSet();

            IRMarketDataParser.serializeToJsonDatPoints(shiftedScenario.getDataPoints());

            Thread.sleep(simulationFrequencySec);
        }
    }*/

	public void writeDataset(String importDir, CalibrationDataset s, boolean isOvernightFixing) throws IOException {
		String json = s.serializeToJson();
		String timeStamp = s.getDate().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
		logger.info("Consumer MarketDataStorage: Stored Market Data at: " + timeStamp);
		Path path = Paths.get("C:\\Temp\\marketdata\\md_" + timeStamp + ".json");
		Files.write(path, json.getBytes());
	}

	public void closeStreamsAndLogoff(WebSocket webSocket) {
		logger.info("Virtual CLOSE sent.");
	}

	private CalibrationDataset getShiftedReferenceSet() {
		double randomShiftBp = ThreadLocalRandom.current().nextDouble(-1, 1) / 10000;
		Set<CalibrationDataItem> shifted = this.referenceSet.getDataPoints().stream().map(datapoint -> datapoint.getClonedShifted(1 + randomShiftBp)).collect(Collectors.toSet());
		CalibrationDataset set = new CalibrationDataset(shifted, this.referenceSet.getDate());
		return set;
	}

}
