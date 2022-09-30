package net.finmath.smartcontract.marketdata;


import net.finmath.smartcontract.marketdata.util.RunnableJobTimeEvent;

import java.time.LocalTime;


public class DemoLauncherScheduled {


	public static void main(String[] args) throws Exception {
		final String newLine = System.getProperty("line.separator");

		final RunnableJobTimeEvent summaryReportJob = RunnableJobTimeEvent.builder
				.addTimingParams(LocalTime.of(12,54,0)).addPeriodInSec(60).build();
		summaryReportJob.run();

		//new DemoLauncher2().run();
	}



}    
