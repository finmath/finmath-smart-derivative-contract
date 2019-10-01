package net.finmath.smartcontract.demo.plotgeneration;

import java.awt.event.ActionEvent;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;

import net.finmath.smartcontract.demo.chartdatageneration.ChartData;
import net.finmath.smartcontract.demo.chartdatageneration.ChartDataGenerator;


/**
 *  A simple generator for generating a time series chart
 *
 * @author Peter Kohl-Landgraf
 */
public class TimeSeriesChartGenerator implements PlotGenerator {
	private final ChartDataGenerator chartDataGenerator;

	public TimeSeriesChartGenerator(final ChartDataGenerator chartDataGenerator){
		this.chartDataGenerator=chartDataGenerator;
	}

	@Override
	public CategoryPlot createPlot(final ActionEvent e) {
		final ChartData chartData = this.chartDataGenerator.generatedChartData(e);

		final CategoryDataset dataset = (CategoryDataset) chartData.getDataset();
		final LineAndShapeRenderer renderer1 = new LineAndShapeRenderer();
		renderer1.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());

		final NumberAxis rangeAxis1 = new NumberAxis("Market Value");
		rangeAxis1.setAutoRange(true);

		return new CategoryPlot(dataset, null, rangeAxis1, renderer1);
	}
}
