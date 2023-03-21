/*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christianfries.com.
 *
 * Created on 21 May 2018
 */

package net.finmath.smartcontract.plots;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import net.finmath.plots.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;

/**
 * Small convenient wrapper for Java FX line plot.
 *
 * @author Christian Fries
 */
public class Plot2DFX implements Plot {

	private List<Plotable2D> plotables;

	private String title = "";
	private String xAxisLabel = "x";
	private String yAxisLabel = "y";
	private NumberFormat xAxisNumberFormat;
	private NumberFormat yAxisNumberFormat;
	private Boolean isLegendVisible = false;

	private transient JFrame frame;
	private LineChart<Number,Number> chart;
	private final Object updateLock = new Object();

	public Plot2DFX(final List<Plotable2D> plotables) {
		super();
		this.plotables = plotables;
	}

	public Plot2DFX(final double xmin, final double xmax, final int numberOfPointsX, final DoubleUnaryOperator function) {
		this(xmin, xmax, numberOfPointsX, Collections.singletonList(new Named<DoubleUnaryOperator>("",function)));
	}

	public Plot2DFX(final double xmin, final double xmax, final int numberOfPointsX, final List<Named<DoubleUnaryOperator>> doubleUnaryOperators) {
		this(doubleUnaryOperators.stream().map(namedFunction -> { return new PlotableFunction2D(xmin, xmax, numberOfPointsX, namedFunction, null); }).collect(Collectors.toList()));
	}

	public Plot2DFX() {
		this(null);
	}

	private void init() {
		//defining the axes
		final NumberAxis xAxis = new NumberAxis();
		final NumberAxis yAxis = new NumberAxis();
		xAxis.setLabel(xAxisLabel);
		yAxis.setLabel(yAxisLabel);
		//creating the chart
		chart = new LineChart<Number,Number>(xAxis,yAxis);
		update();
	}

	private void update() {
		Platform.runLater(new Runnable() {
			@Override public void run() {
				if(plotables == null) {
					return;
				}
				chart.setTitle(title);
				for(int functionIndex=0; functionIndex<plotables.size(); functionIndex++) {
					final Plotable2D plotable = plotables.get(functionIndex);
					final GraphStyle style = plotable.getStyle();
					Color color = getColor(style);
					if(color == null) {
						color = getDefaultColor(functionIndex);
					}

					final String rgba = String.format("%d, %d, %d, %f", (int)(color.getRed() * 255), (int)(color.getGreen() * 255), (int)(color.getBlue() * 255), (float)color.getOpacity());

					final List<Point2D> plotableSeries = plotable.getSeries();
					XYChart.Series series = null;
					if(functionIndex < chart.getData().size()) {
						series = chart.getData().get(functionIndex);
					}
					if(series == null) {
						series = new XYChart.Series();
						chart.getData().add(functionIndex,series);
					}
					series.setName(plotable.getName());
					for(int i = 0; i<plotableSeries.size(); i++) {
						XYChart.Data<Number, Number> data = null;
						if(i < series.getData().size()) {
							data = (Data<Number, Number>) series.getData().get(i);
						}
						if(data == null) {
							data = new XYChart.Data(plotableSeries.get(i).getX(), plotableSeries.get(i).getY());
							if(style != null && style.getShape() != null) {
								//								data.setNode(new javafx.scene.shape.Rectangle(10,10, color));
								data.setNode(new javafx.scene.shape.Circle(6, color));

							}
							series.getData().add(i, data);
						}
						data.setXValue(plotableSeries.get(i).getX());
						data.setYValue(plotableSeries.get(i).getY());
					}

					/*
					 * Apply style to line
					 */
					if(style != null && style.getStroke() != null) {
						series.getNode().setStyle("-fx-stroke: rgba(" + rgba + ");");
					} else {
						series.getNode().setStyle("-fx-stroke: none;");
					}

					/*
			String rgb = String.format("%d, %d, %d", (int) (color.getRed() * 255), (int) (color.getGreen() * 255),(int) (color.getBlue() * 255));
			series.getNode().setStyle("-fx-stroke: rgba(" + rgb + ",1.0);  -fx-background-color: #FF0000, white;");
			series.getNode().setStyle("-fx-stroke: rgba(" + rgb + ",1.0);  -fx-background-color: #FF0000, white;");
					 */
					//			lineChart.setStyle("-fx-create-symbols: false;");

					//			.default-color2.chart-line-symbol { -fx-background-color: #dda0dd, white; }
				}
				final Node[] legendItems = chart.lookupAll(".chart-legend-item-symbol").toArray(new Node[0]);
				for(int i = 0; i<legendItems.length; i++) {
					final Node legendItemNode = legendItems[i];
					Color color = getColor(plotables.get(i).getStyle());
					if(color == null) {
						color = getDefaultColor(i);
					}

					final String rgba = String.format("%d, %d, %d, %f", (int)(color.getRed() * 255), (int)(color.getGreen() * 255), (int)(color.getBlue() * 255), (float)color.getOpacity());
					legendItemNode.setStyle("-fx-background-color: rgba("+rgba+");");
					chart.applyCss();
				}
				chart.applyCss();
			}
		});
	}

	private Color getColor(final GraphStyle style) {
		final java.awt.Color awtColor = style != null ? style.getColor() : null;
		Color color = null;
		if(awtColor != null) {
			color = new Color(awtColor.getRed()/255.0, awtColor.getGreen()/255.0, awtColor.getBlue()/255.0, awtColor.getAlpha()/255.0);
		}
		return color;
	}

	private Color getDefaultColor(final int functionIndex) {
		switch (functionIndex) {
			case 0:
				return new Color(1.0, 0,  0, 1.0);
			case 1:
				return new Color(0, 1.0,  0, 1.0);
			case 2:
				return new Color(0, 0,  1.0, 1.0);
			default:
				return new Color(0, 0,  0, 1.0);
		}
	}

	@Override
	public void show() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// This method is invoked on Swing thread
				if(frame != null) frame.dispose();

				frame = new JFrame("FX");
				final JFXPanel fxPanel = new JFXPanel();
				frame.add(fxPanel);
				frame.setVisible(true);
				frame.setSize(800, 600);
				//				frame.setSize(960, 540+22);

				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						init();

						fxPanel.setScene(new Scene(chart, 800,600));
						//						fxPanel.setScene(new Scene(chart,960,540+22));
					}
				});
				update();
			}
		});
	}

	@Override
	public void close() {
		synchronized (updateLock) {
			if(frame != null) frame.dispose();
		}
	}

	public Chart get() {
		init();
		return chart;
	}

	@Override
	public Plot2DFX saveAsJPG(final File file, final int width, final int height) throws IOException {
		throw new UnsupportedOperationException();
	}

	public Plot2DFX saveAsPNG(final File file, final int width, final int height) throws IOException {
		if(chart == null) {
			return this;
		}
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {

					chart.setAnimated(false);

					final OutputStream out = new BufferedOutputStream(new FileOutputStream(file));

					final BufferedImage imageWithAlpha = new BufferedImage(width, height,BufferedImage.TYPE_INT_ARGB);
					final Graphics2D g2 = imageWithAlpha.createGraphics();
					final Rectangle2D r2D = new Rectangle2D.Double(0, 0, width, height);

					final WritableImage image = chart.getScene().snapshot(null);
					ImageIO.write(javafx.embed.swing.SwingFXUtils.fromFXImage(image, null), "png", out);

					/*
		// Strip alpha channel
		BufferedImage imageWithoutAlpha = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);

		Graphics2D graphics = imageWithoutAlpha.createGraphics();
		graphics.drawImage(imageWithAlpha, null, 0, 0);

		ImageIO.write(imageWithoutAlpha, "jpg", out);
					 */

					out.close();

				} catch (final IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}});
		return this;
	}

	@Override
	public Plot2DFX saveAsPDF(final File file, final int width, final int height) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Plot2DFX saveAsSVG(final File file, final int width, final int height) throws IOException {
		throw new UnsupportedOperationException();
	}

	public Plot2DFX update(final List<Plotable2D> plotables) {
		this.plotables = plotables;
		synchronized (updateLock) {
			if(chart != null) {
				update();
			}
		}
		return this;
	}

	@Override
	public Plot2DFX setTitle(final String title) {
		this.title = title;
		return this;
	}

	@Override
	public Plot2DFX setXAxisLabel(final String xAxisLabel) {
		this.xAxisLabel = xAxisLabel;
		return this;
	}

	@Override
	public Plot2DFX setYAxisLabel(final String yAxisLabel) {
		this.yAxisLabel = yAxisLabel;
		return this;
	}

	public Plot2DFX setYAxisRange(final Double min, final Double max) {
		if(chart == null || chart.getYAxis() == null) {
			return this;
		}
		if(min == null || max == null) {
			chart.getYAxis().setAutoRanging(true);
		}
		else {
			chart.getYAxis().setAutoRanging(false);
			((NumberAxis)chart.getYAxis()).setLowerBound(min);
			((NumberAxis)chart.getYAxis()).setUpperBound(max);
		}
		return this;
	}

	@Override
	public Plot setZAxisLabel(final String zAxisLabel) {
		throw new UnsupportedOperationException("The 2D plot does not suport a z-axis. Try 3D plot instead.");
	}

	/**
	 * @param isLegendVisible the isLegendVisible to set
	 */
	@Override
	public Plot setIsLegendVisible(final Boolean isLegendVisible) {
		this.isLegendVisible = isLegendVisible;
		return this;
	}
}
