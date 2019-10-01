package net.finmath.smartcontract.demo;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.swing.Timer;

import net.finmath.smartcontract.contract.SmartDerivativeContractSchedule;

/**
 *  Very basic schedule simulator which maps an eventtime on a schedule time
 *
 * @author Peter Kohl-Landgraf
 */
public class DemoScheduleSimulator implements ActionListener{

	private final SmartDerivativeContractSchedule schedule;
	private final Timer timer = new Timer(1000, this);
	private final LocalDateTime initTime;
	private int eventTimeIndex;

	public DemoScheduleSimulator(final SmartDerivativeContractSchedule schedule){
		this.schedule=schedule;
		this.initTime=LocalDateTime.now();
		timer.start();
		eventTimeIndex = 0;
	}

	public void actionPerformed(final ActionEvent event)
	{
		final SmartDerivativeContractSchedule.EventTimes actualEventTime = this.schedule.getEventTimes().get(eventTimeIndex);
		final LocalDateTime eventTime =
				LocalDateTime.ofInstant(Instant.ofEpochMilli(event.getWhen()), ZoneId.systemDefault());
		final int index = (int) initTime.until(eventTime,SECONDS);
		final int eventIndex = index % 3;
		if ( eventIndex==0) {
			System.out.println(index + " - " + actualEventTime.getMarginCheckTime());
		}
		else if ( eventIndex==1) {
			System.out.println(index + " - " + actualEventTime.getSettementTime());
		}
		else if ( eventIndex ==2) {
			System.out.println(index + " - " + actualEventTime.getAccountAccessAllowedStart().plus(actualEventTime.getAccountAccessAllowedPeriod()));
			this.eventTimeIndex++;
		}
	}
}
