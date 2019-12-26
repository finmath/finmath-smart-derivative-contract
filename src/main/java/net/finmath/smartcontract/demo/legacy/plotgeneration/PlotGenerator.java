package net.finmath.smartcontract.demo.legacy.plotgeneration;

import java.awt.event.ActionEvent;

import org.jfree.chart.plot.CategoryPlot;


/**
 *  Interface for generationg a JFREEChart
 *
 * @author Peter Kohl-Landgraf
 */
public interface PlotGenerator {
	/**
	 *
	 * @param event action event
	 * @return a new plot
	 */
	CategoryPlot createPlot(final ActionEvent event);

}
