/*
 * (c) Copyright Christian P. Fries, Germany. All rights reserved. Contact: email@christianfries.com.
 *
 * Created on 16 Oct 2018
 */

package net.finmath.smartcontract.contract;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author Christian Fries
 *
 */
public class SmartDerivativeContractScheduleGeneratorTest {

	@Test
	public void test() {
		LocalDate startDate = LocalDate.of(2018, 9, 15);
		LocalDate maturity = LocalDate.of(2028, 9, 15);
		LocalTime settlementTime = LocalTime.of(17, 30);
		Duration accountAccessAllowedDuration = Duration.ofSeconds(10*60);
		
		SmartDerivativeContractSchedule schedule = SmartDerivativeContractScheduleGenerator.getScheduleForBusinessDays("target2", startDate, maturity, settlementTime, accountAccessAllowedDuration);
		
		for(SmartDerivativeContractSchedule.EventTimes event : schedule.getEventTimes()) {
			LocalDateTime settementTime = event.getSettementTime();
			LocalDateTime accountAccessAllowedStart = event.getAccountAccessAllowedStart();
			LocalDateTime accountAccessAllowedEnd = event.getAccountAccessAllowedStart().plusSeconds(event.getAccountAccessAllowedPeriod().getSeconds());
			LocalDateTime marginCheckTime = event.getMarginCheckTime();

			System.out.println("Settlement............:" + settementTime);
			System.out.println("Account access start..:" + accountAccessAllowedStart);
			System.out.println("Account access end....:" + accountAccessAllowedEnd);
			System.out.println("Margin check..........:" + marginCheckTime);
			System.out.println();
			
			Assert.assertTrue("Access after settlement", accountAccessAllowedStart.isAfter(settementTime));
			Assert.assertTrue("Account access", accountAccessAllowedEnd.isAfter(accountAccessAllowedStart));
			Assert.assertTrue("Margin check after account access", marginCheckTime.isAfter(accountAccessAllowedEnd));
		}
	}

}
