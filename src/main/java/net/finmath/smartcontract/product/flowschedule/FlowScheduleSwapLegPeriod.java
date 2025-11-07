package net.finmath.smartcontract.product.flowschedule;


import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import net.finmath.smartcontract.valuation.marketdata.data.LocalDateTimeAdapter;

import java.time.LocalDateTime;


/**
 * Object for structuring the flow schedule of a swap leg period as an XML string.
 * Contains the rolled-out schedule for the period and information on cash-flow.
 *
 * @author  Raphael Prandtl
 */
@XmlRootElement(name = "flowScheduleSwapLegPeriod")
@XmlType(name = "", propOrder = {
		"fixingDate",
		"periodStartDate",
		"periodEndDate",
		"paymentDate",
		"notional",
		"rate",
		"flow",
		"npv"
})
public class FlowScheduleSwapLegPeriod {
	LocalDateTime 	fixingDate;
	LocalDateTime 	periodStartDate;
	LocalDateTime 	periodEndDate;
	LocalDateTime 	paymentDate;
	double		  	notional;
	double 		 	rate;
	double 		  	flow;
	double 			npv;

	@XmlElement(name = "fixingDate")
	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	public LocalDateTime getFixingDate() {
		return fixingDate;
	}

	public void setFixingDate(final LocalDateTime fixingDate) {
		this.fixingDate = fixingDate;
	}

	@XmlElement(name = "periodStartDate")
	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	public LocalDateTime getPeriodStartDate() {
		return periodStartDate;
	}

	public void setPeriodStartDate(final LocalDateTime periodStartDate) {
		this.periodStartDate = periodStartDate;
	}

	@XmlElement(name = "periodEndDate")
	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	public LocalDateTime getPeriodEndDate() {
		return periodEndDate;
	}

	public void setPeriodEndDate(final LocalDateTime periodEndDate) {
		this.periodEndDate = periodEndDate;
	}

	@XmlElement(name = "paymentDate")
	@XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
	public LocalDateTime getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(final LocalDateTime paymentDate) {
		this.paymentDate = paymentDate;
	}

	@XmlElement(name = "notional")
	public double getNotional() {
		return notional;
	}

	public void setNotional(final double notional) {
		this.notional = notional;
	}

	@XmlElement(name = "rate")
	public double getRate() {
		return rate;
	}

	public void setRate(final double rate) {
		this.rate = rate;
	}

	@XmlElement(name = "flow")
	public double getFlow() {
		return flow;
	}

	public void setFlow(final double flow) {
		this.flow = flow;
	}

	@XmlElement(name = "npv")
	public double getNpv() {
		return npv;
	}

	public void setNpv(final double npv) {
		this.npv = npv;
	}

}
