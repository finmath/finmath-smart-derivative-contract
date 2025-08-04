package net.finmath.smartcontract.product.flowschedule;


import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.List;


/**
 * Object for structuring the flow schedule of a swap leg as an XML string.
 * Contains the legType and a list of {@link FlowScheduleSwapLegPeriod},
 * each representing the flow schedule of a single period.
 *
 * @author  Raphael Prandtl
 */
@XmlRootElement(name = "flowScheduleSwapLeg")
@XmlType(name = "", propOrder = {
		"legType",
		"flowScheduleSwapLegPeriods"
})
public class FlowScheduleSwapLeg {
	String legType;
	List<FlowScheduleSwapLegPeriod> flowScheduleSwapLegPeriods;

	@XmlElement(name = "legType")
	public String getLegType() {
		return legType;
	}

	public void setLegType(final String legType) {
		this.legType = legType;
	}

	@XmlElementWrapper(name = "flowScheduleSwapLegPeriods")
	@XmlElement(name = "flowScheduleSwapLegPeriod")
	public List<FlowScheduleSwapLegPeriod> getFlowScheduleSwapLegPeriods() {
		return flowScheduleSwapLegPeriods;
	}

	public void setFlowScheduleSwapLegPeriods(final List<FlowScheduleSwapLegPeriod> flowScheduleSwapLegPeriods) {
		this.flowScheduleSwapLegPeriods = flowScheduleSwapLegPeriods;
	}

}
