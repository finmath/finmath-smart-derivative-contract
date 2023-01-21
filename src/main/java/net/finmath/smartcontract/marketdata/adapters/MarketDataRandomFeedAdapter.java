package net.finmath.smartcontract.marketdata.adapters;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDatapoint;
import net.finmath.smartcontract.marketdata.util.IRMarketDataParser;
import net.finmath.smartcontract.marketdata.util.IRMarketDataSet;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MarketDataRandomFeedAdapter {

    LocalDateTime endTime;
    IRMarketDataSet referenceSet;

    int simulationFrequencySec;
    public MarketDataRandomFeedAdapter(Period processingPeriod, String referenceMarketDataJson) throws Exception{
        this.endTime = LocalDateTime.now().plus(processingPeriod);
        referenceSet = IRMarketDataParser.getScenariosFromJsonString(referenceMarketDataJson).get(0);
        simulationFrequencySec = 3;
    }


    Observable<String> asObservable(){
        ObservableOnSubscribe<String> observable = emitter ->{
            while (LocalDateTime.now().isBefore(endTime)) {
                IRMarketDataSet shiftedScenario = getShiftedReferenceSet();
                String json = IRMarketDataParser.serializeToJsonDatPoints(shiftedScenario.getDataPoints());
                emitter.onNext(json);
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

    private IRMarketDataSet getShiftedReferenceSet(){
        double randomShiftBp = ThreadLocalRandom.current().nextDouble(-1,1) / 10000;
        Set< CalibrationDatapoint> shifted = this.referenceSet.getDataPoints().stream().map(datapoint->datapoint.getClonedShifted(1+randomShiftBp)).collect(Collectors.toSet());
        IRMarketDataSet set = new IRMarketDataSet(shifted,this.referenceSet.getDate());
        return set;
    }

}
