/*
 * (c) Copyright Christian P. Fries, Germany. All rights reserved. Contact: email@christianfries.com.
 *
 * Created on 21 May 2018
 */
package net.finmath.smartcontract.demo;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.Chart;
import javafx.scene.layout.FlowPane;
import javafx.scene.Node;
import javafx.scene.Group;
import net.finmath.marketdata.products.Swap;
import net.finmath.montecarlo.BrownianMotion;
import net.finmath.montecarlo.BrownianMotionInterface;
import net.finmath.plots.Category2D;
import net.finmath.plots.GraphStyle;
import net.finmath.plots.Named;
import net.finmath.plots.Plot2D;
import net.finmath.plots.Plot2DBarFX;
import net.finmath.plots.Plot2DFX;
import net.finmath.plots.Plotable2D;
import net.finmath.plots.PlotableCategories;
import net.finmath.plots.PlotableFunction2D;
import net.finmath.plots.PlotablePoints2D;
import net.finmath.plots.Point2D;
import net.finmath.plots.demo.SmartDerivativeContractVisualization;
import net.finmath.smartcontract.demo.chartdatageneration.ChartData;
import net.finmath.smartcontract.demo.chartdatageneration.ChartDataGeneratorMarketValue;
import net.finmath.smartcontract.demo.chartdatageneration.ChartDataGeneratorSDCAccountBalance;
import net.finmath.smartcontract.oracle.ValuationOraclePlainSwapHistoricScenarios;
import net.finmath.smartcontract.simulation.products.IRSwapGenerator;
import net.finmath.smartcontract.simulation.scenariogeneration.IRMarketDataScenario;
import net.finmath.smartcontract.simulation.scenariogeneration.IRScenarioGenerator;
import net.finmath.stochastic.RandomVariableInterface;
import net.finmath.time.TimeDiscretization;

/**
 * Plots the regression estimation of a curve.
 *
 * @author Christian Fries
 */
public class VisualiserSDC {

	private List<Point2D> seriesMarketValues;
	Plot2DBarFX plot;
	Plot2DFX plot2;
	ChartDataGeneratorSDCAccountBalance accountBalanceGenerator;
	ChartDataGeneratorMarketValue marketValueDataGenerator;

	/**
	 * Run the demo.
	 *
	 * @param args Not used.
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {

		LocalDate startDate = LocalDate.of(2007, 1, 1);
		LocalDate maturity = LocalDate.of(2012, 1, 3);
		String fileName = "timeseriesdatamap.json";
		DateTimeFormatter providedDateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
		final List<IRMarketDataScenario> scenarioList = IRScenarioGenerator.getScenariosFromJsonFile(fileName,providedDateFormat).stream().filter(S->S.getDate().toLocalDate().isAfter(startDate)).filter(S->S.getDate().toLocalDate().isBefore(maturity)).collect(Collectors.toList());

		double notional = 1.0E7;
		String MaturityKey = "5Y";
		String forwardCurveKey = "forward-EUR-6M";
		String discountCurveKey = "discount-EUR-OIS";
		LocalDate productStartDate = scenarioList.get(0).getDate().toLocalDate();

		double fixRate = scenarioList.get(0).getCurveData("Euribor6M").getDataPointStreamForProductType("Swap-Rate").filter(e->e.getMaturity().equals(MaturityKey)).mapToDouble(e->e.getQuote()).findAny().getAsDouble() / 100.;
		Swap swap = IRSwapGenerator.generateAnalyticSwapObject(productStartDate,MaturityKey,fixRate,true,forwardCurveKey,discountCurveKey);

		ValuationOraclePlainSwapHistoricScenarios oracle = new ValuationOraclePlainSwapHistoricScenarios(swap,notional,scenarioList);

		List<LocalDateTime> scenarioDates = scenarioList.stream().map(scenario->scenario.getDate()).collect(Collectors.toList());

		ChartDataGeneratorSDCAccountBalance chartDataGeneratorSDCAccountBalance = new ChartDataGeneratorSDCAccountBalance(30000,oracle,scenarioDates);
		ChartDataGeneratorMarketValue marketValueGenerator = new ChartDataGeneratorMarketValue(oracle,scenarioDates);

		VisualiserSDC sdcVisual = new VisualiserSDC();
		sdcVisual.start();



		Double marketValue = 0.0;
		for(int i=0; i<100; i++) {
			double marginCall = i==0. ? oracle.getValue(scenarioDates.get(0)) :  oracle.getValue(scenarioDates.get(i)) -  oracle.getValue(scenarioDates.get(i-1));//90*(new Random()).nextDouble()-45;
			double marginBuffer = 45000;
			sdcVisual.updateWithValue(marginBuffer, i /* Date index */, marketValue, marginCall);
			marketValue += marginCall;
			// The null will result in no update for the market value plot
			sdcVisual.updateWithValue(marginBuffer, i, null, 0);
		}
	}

	public void start() throws Exception {

		seriesMarketValues = new ArrayList<>();

		plot = new Plot2DBarFX(null,
				"Smart Contract Accounts",
				"Account",
				"Value",
				new DecimalFormat("####.00"),
				0.0,
				1.0E5,
				1.0E4, false);

		plot2 = new Plot2DFX();
		plot2.setIsLegendVisible(false);
		plot2.setTitle("Market Value");
		plot2.setXAxisLabel("Date");
		plot2.setYAxisLabel("Market Value");

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// This method is invoked on Swing thread
				JFrame frame = new JFrame("FX");
				final JFXPanel fxPanel = new JFXPanel();
				frame.add(fxPanel);
				frame.setVisible(true);
				frame.setSize(1600, 600);
				//				frame.setSize(960, 540+22);

				Platform.runLater(new Runnable() {
					@Override
					public void run() {

						FlowPane root = new FlowPane();
						root.getChildren().addAll(new Group(plot.get()), plot2.get());


						Scene scene = new Scene(root, 1600, 600);
						scene.getStylesheets().add("barchart.css");
						fxPanel.setScene(scene);
					}
				});
			}
		});

	}

	void updateWithValue(double base, double x, Double value, double increment) throws InterruptedException {
		List<Category2D> density1 = new ArrayList<>();
		density1.add(new Category2D("We", base+Math.min(0,+increment)));
		density1.add(new Category2D("Counterpart", base+Math.min(0,-increment)));

		List<Category2D> density2 = new ArrayList<>();
		density2.add(new Category2D("We", -Math.min(0,+increment)));
		density2.add(new Category2D("Counterpart", -Math.min(0,-increment)));

		List<Category2D> density3 = new ArrayList<>();
		density3.add(new Category2D("We", Math.max(0,+increment)));
		density3.add(new Category2D("Counterpart", Math.max(0,-increment)));

		List<PlotableCategories> plotables = new ArrayList<>();
		plotables.add(new PlotableCategories() {

			@Override
			public String getName() {
				return "Margin";
			}

			@Override
			public GraphStyle getStyle() {
				return new GraphStyle(new Ellipse2D.Float(-1.0f,-1.0f,2.0f,2.0f), new BasicStroke(1.0f), new Color(0.0f, 0.0f, 1.0f));
			}

			@Override
			public List<Category2D> getSeries() {
				return density1;
			}
		});

		plotables.add(new PlotableCategories() {

			@Override
			public String getName() {
				return "Pay";
			}

			@Override
			public GraphStyle getStyle() {
				return null;
			}

			@Override
			public List<Category2D> getSeries() {
				return density2;
			}
		});
		plotables.add(new PlotableCategories() {

			@Override
			public String getName() {
				return "Receive";
			}

			@Override
			public GraphStyle getStyle() {
				return null;
				//		return new GraphStyle(new Ellipse2D.Float(-1.0f,-1.0f,2.0f,2.0f), new BasicStroke(1.0f), new Color(0.0f, 0.0f, 1.0f));
			}

			@Override
			public List<Category2D> getSeries() {
				return density3;
			}
		});

		plot.update(plotables);

		if(value != null) {
			List<Plotable2D> plotables2 = new ArrayList<>();
			plotables2.add(new Plotable2D() {

				@Override
				public String getName() {
					return "Market Value";
				}

				@Override
				public GraphStyle getStyle() {
					return new GraphStyle(new Ellipse2D.Float(-3.0f,-3.0f,6.0f,6.0f), new BasicStroke(1.0f), new Color(1.0f, 0.0f, 0.0f));
				}

				@Override
				public List<Point2D> getSeries() {
					return seriesMarketValues;
				}
			});

			seriesMarketValues.add(new Point2D(x, value));

			plot2.update(plotables2);
		}

		Thread.sleep(500);
	}
}
