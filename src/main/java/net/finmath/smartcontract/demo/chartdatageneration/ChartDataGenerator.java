package net.finmath.smartcontract.demo.chartdatageneration;

import java.awt.event.ActionEvent;


/**
 *  Interface for generationg a the underliyng dataset for a JFREEChart
 *
 * @author Peter Kohl-Landgraf
 */
public interface ChartDataGenerator {

	/**
	 *
	 * @param event The ActionEvent which triggers the data geneartion
	 * @return the new generated chart data object
	 */
	ChartData generatedChartData(final ActionEvent event);
}
