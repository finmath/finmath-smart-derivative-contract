package net.finmath.smartcontract.demo.plotgeneration;

import net.finmath.smartcontract.demo.chartdatageneration.ChartData;
import net.finmath.smartcontract.demo.chartdatageneration.ChartDataGenerator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.List;


/**
 *  A simple generator for generating a stacked bar chart
 *
 * @author Peter Kohl-Landgraf
 */
public class StackedBarchartGenerator implements PlotGenerator {

	private final ChartDataGenerator chartDataGenerator;
	private final Color backGroundPaintColor = new Color(249, 231, 236);


	public StackedBarchartGenerator(ChartDataGenerator chartDataGenerator) {
		this.chartDataGenerator = chartDataGenerator;
	}

	@Override
	public CategoryPlot createPlot(final ActionEvent e) {

		ChartData chartData = this.chartDataGenerator.generatedChartData(e);

		CategoryDataset categoryDataset = (CategoryDataset) chartData.getDataset();

		final JFreeChart chart = ChartFactory.createStackedBarChart(
				chartData.getPropertyChartTitle(), "", "Balance",
				categoryDataset, PlotOrientation.VERTICAL, false, true, false);

		chart.setBackgroundPaint(backGroundPaintColor);

		CategoryPlot plot = chart.getCategoryPlot();
		List<Color> colorList = chartData.getPropertyColorListStackedBar();
		plot.getRenderer().setSeriesPaint(0, colorList.get(0));
		plot.getRenderer().setSeriesPaint(1, colorList.get(1));
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setRange(0, 100000);

		return plot;
	}
}
