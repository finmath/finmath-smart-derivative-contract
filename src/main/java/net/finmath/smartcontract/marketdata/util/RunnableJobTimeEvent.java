package net.finmath.smartcontract.marketdata.util;

import net.finmath.marketdata.adapters.MarketdataItem;
import net.finmath.marketdata.products.Swap;
import net.finmath.smartcontract.oracle.historical.ValuationOraclePlainSwapHistoricScenarios;
import net.finmath.smartcontract.simulation.products.IRSwapGenerator;
import net.finmath.smartcontract.simulation.scenariogeneration.IRMarketDataScenario;
import net.finmath.smartcontract.simulation.scenariogeneration.IRScenarioGenerator;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RunnableJobTimeEvent implements Runnable {

    private long periodInSec;
    private LocalTime atTime;
//    private InterfaceReportProcess reportProcess;
    public final static Builder builder = new Builder();
    final   TimerTask   task = new TimerTask() {
        @Override
        public void run() {
            try {
                System.out.println(LocalTime.now().format(DateTimeFormatter.ISO_TIME) + ": ScheduledExecTime: " + task.scheduledExecutionTime() + ": Starting Process ");
                start();
                System.out.println(LocalTime.now().format(DateTimeFormatter.ISO_TIME) + ": Process  Finished.");
            } catch (Exception e) {
                System.out.println("Catched Exception " + e);
            }
        }
    };

    private void start(){

        try {
            Set<MarketdataItem> mdItemList = MDRetrieveUtil.getRICS("rics_spec.txt", "refinitiv_connect.properties");
            LocalDateTime time = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-hh-mm-ss");
            String asStr = time.format(formatter);
            String mdJson = MDRetrieveUtil.constructJSON(mdItemList);
            String fileName = "C:\\Temp\\md_"+asStr+".json";
            Files.writeString(Paths.get(fileName),mdJson);
            System.out.println(mdJson);

            final LocalDate startDate = LocalDate.now();
            final LocalDate maturity = LocalDate.of(2012, 1, 3);
            //final String fileName = "timeseriesdatamap.json";
            final DateTimeFormatter providedDateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
            final List<IRMarketDataScenario> scenarioList = IRScenarioGenerator.getScenariosFromJsonFile(fileName,providedDateFormat);//.stream().filter(S->S.getDate().toLocalDate().isAfter(startDate)).filter(S->S.getDate().toLocalDate().isBefore(maturity)).collect(Collectors.toList());
            // CSV Method returns same List
            // final List<IRMarketDataScenario> scenarioList = IRScenarioGenerator.getScenariosFromCSVFile(fileName,providedDateFormat).stream().filter(S->S.getDate().toLocalDate().isAfter(startDate)).filter(S->S.getDate().toLocalDate().isBefore(maturity)).collect(Collectors.toList());


            final double notional = 1.0E6;
            final String maturityKey = "10Y";
            final String forwardCurveKey = "forward-EUR-6M";
            final String discountCurveKey = "discount-EUR-OIS";
            final LocalDate productStartDate = LocalDate.of(2022,9,26);//scenarioList.get(0).getDate().toLocalDate().minusDays(0);

            final double actualRate =  scenarioList.get(0).getCurveData("Euribor6M").getDataPointStreamForProductType("Swap-Rate").filter(e->e.getMaturity().equals(maturityKey)).mapToDouble(e->e.getQuote()).findAny().getAsDouble() ;
            final double actualRateOIS =  scenarioList.get(0).getCurveData("ESTR").getDataPointStreamForProductType("Swap-Rate").filter(e->e.getMaturity().equals(maturityKey)).mapToDouble(e->e.getQuote()).findAny().getAsDouble() ;


            final double fixRate = 0.026795;// scenarioList.get(0).getCurveData("Euribor6M").getDataPointStreamForProductType("Swap-Rate").filter(e->e.getMaturity().equals(maturityKey)).mapToDouble(e->e.getQuote()).findAny().getAsDouble() ;
            final Swap swap = IRSwapGenerator.generateAnalyticSwapObject(productStartDate, maturityKey, fixRate, true, forwardCurveKey,discountCurveKey);

            final ValuationOraclePlainSwapHistoricScenarios oracle = new ValuationOraclePlainSwapHistoricScenarios(swap,notional,scenarioList);
            double value = oracle.getValue(scenarioList.get(0).getDate(),scenarioList.get(0).getDate());
            System.out.println(value);
            String line = asStr + "," + actualRateOIS + "," + actualRate + "," +value + System.getProperty("line.separator");;

            try {
                Files.write(Paths.get("C:\\Temp\\valuation.txt"), line.getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                System.err.println(e);
            }

        }
        catch (Exception e){

        }
    }


    public RunnableJobTimeEvent(){}


    public static class Builder {
        private long periodInSec;
        private LocalTime atTime;
//        private InterfaceReportProcess reportGenerator;

        public RunnableJobTimeEvent build() {
            RunnableJobTimeEvent executor = new RunnableJobTimeEvent();
            executor.periodInSec = this.periodInSec;
            executor.atTime = this.atTime;
//            executor.reportProcess = this.reportGenerator;
            return executor;
        }

        /*public Builder attachReportProcess(InterfaceReportProcess reportGenerator) {
            this.reportGenerator = reportGenerator;
            return this;
        }*/

        public Builder addTimingParams(LocalTime atTime) {
            this.atTime = atTime;
            return this;
        }

        public Builder addPeriodInSec(long periodInSec) {
            this.periodInSec = periodInSec;
            return this;
        }
    }


    public void run(){
        System.out.println("Time-Event Based Executor started - with process  on first time: " + this.atTime.toString() );
        Timer timer = new Timer();

        final LocalDateTime actualDayExecutionDateTime  = LocalDateTime.of(LocalDate.now(),this.atTime);
        LocalDateTime nextExcecutionDateTime = actualDayExecutionDateTime;

        if (this.atTime.isBefore(LocalTime.now())) {
            task.run();
            nextExcecutionDateTime = new BusinessdayCalendarExcludingTARGETHolidays().getRolledDate(actualDayExecutionDateTime.toLocalDate(),1).atTime(this.atTime);

        }
        long miliSec =  (periodInSec > 0) ? periodInSec*1000 : 24*60*60*1000;
        Date firstDateTime = Date.from(nextExcecutionDateTime.atZone(ZoneId.systemDefault()).toInstant());
        timer.schedule(task,firstDateTime, miliSec);

    }
}
