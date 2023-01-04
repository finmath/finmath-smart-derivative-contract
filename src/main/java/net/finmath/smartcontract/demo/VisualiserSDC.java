package net.finmath.smartcontract.demo;


import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import net.finmath.marketdata.products.Swap;
import net.finmath.plots.*;
import net.finmath.smartcontract.oracle.SmartDerivativeContractSettlementOracle;
import net.finmath.smartcontract.oracle.interestrates.ValuationOraclePlainSwap;
import net.finmath.smartcontract.product.IRSwapGenerator;
import net.finmath.smartcontract.marketdata.util.IRMarketDataParser;
import net.finmath.smartcontract.marketdata.util.IRMarketDataSet;
import net.finmath.smartcontract.plots.Plot2DFX;
import net.finmath.smartcontract.simulation.products.IRSwapGenerator;
import net.finmath.smartcontract.simulation.scenariogeneration.IRMarketDataParser;
import net.finmath.smartcontract.simulation.scenariogeneration.IRMarketDataSet;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Visualization of the settlement using Smart Derivative Contract with a 10Y swap,
 * using a valuation oracle with historic market data.
 * For details see the corresponding white paper at SSRN.
 *
 * @author Christian Fries
 * @author Peter Kohl-Landgraf
 * @author Björn Paffen
 * @author Stefanie Weddigen
 */
public class VisualiserSDC {

	private List<Point2D> seriesMarketValues;

	private Plot2DBarFX plotMarginAccounts;
	private Plot2DFX plotMarketValue;

	/**
	 * Run the demo.
	 *
	 * @param args Not used.
	 * @throws Exception General exception.
	 */
	public static void main(final String[] args) throws Exception {

		final LocalDate startDate = LocalDate.of(2008, 1, 1);
		final LocalDate maturity = LocalDate.of(2012, 1, 3);
		final String fileName = "timeseriesdatamap.json";
		final DateTimeFormatter providedDateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
		final List<IRMarketDataSet> scenarioList = IRMarketDataParser.getScenariosFromJsonFile(fileName).stream().filter(S -> S.getDate().toLocalDate().isAfter(startDate)).filter(S -> S.getDate().toLocalDate().isBefore(maturity)).collect(Collectors.toList());
		// CSV Method returns same List
		// final List<IRMarketDataScenario> scenarioList = IRScenarioGenerator.getScenariosFromCSVFile(fileName,providedDateFormat).stream().filter(S->S.getDate().toLocalDate().isAfter(startDate)).filter(S->S.getDate().toLocalDate().isBefore(maturity)).collect(Collectors.toList());


		final double notional = 1.0E7;
		final String maturityKey = "5Y";
		final String forwardCurveKey = "forward-EUR-6M";
		final String discountCurveKey = "discount-EUR-OIS";
		final LocalDate productStartDate = scenarioList.get(0).getDate().toLocalDate().minusDays(170);

		final double fixRate = scenarioList.get(0).getDataPoints().stream()
				.filter(datapoint->datapoint.getCurveName().equals("Euribor6M") &&
						datapoint.getProductName().equals("Swap-Rate") &&
						datapoint.getMaturity().equals("5Y")).mapToDouble(e -> e.getQuote()).findAny().getAsDouble();

		final Swap swap = IRSwapGenerator.generateAnalyticSwapObject(productStartDate, maturityKey, fixRate, false, forwardCurveKey, discountCurveKey);

		final ValuationOraclePlainSwap oracle = new ValuationOraclePlainSwap(swap, notional, scenarioList);
		final SmartDerivativeContractSettlementOracle margin = new SmartDerivativeContractSettlementOracle(oracle);

		final List<LocalDateTime> scenarioDates = scenarioList.stream().map(scenario -> scenario.getDate()).sorted().collect(Collectors.toList());

		final VisualiserSDC sdcVisual = new VisualiserSDC();
		sdcVisual.start();

		System.out.println("Simulation will start in 5 seconds.");

		Double marketValue = 0.0;
		final double marginBuffer = 120000;
		sdcVisual.updateWithValue(scenarioDates.get(0), 0.0, 0, 0.0, 0, 5000);
		for (int i = 0; i < scenarioDates.size(); i++) {
			long sleepMillis1;
			long sleepMillis2;
			if(i < 11)	{
				sleepMillis1 = 1000;
				sleepMillis2 = 1000;
			}
			else {
				sleepMillis1 = 500;
				sleepMillis2 = 50;
			}

			final double marginCall = i > 0 ? margin.getMargin(scenarioDates.get(i - 1), scenarioDates.get(i)) : 0.0;  // Alternative: use some random values, e.g. 90*(new Random()).nextDouble()-45;
			System.out.println(i + "\t" + DateTimeFormatter.ofPattern("dd.MM.yyyy").format(scenarioDates.get(i)) + "\t" + marginCall);
			marketValue += marginCall;
			sdcVisual.updateWithValue(scenarioDates.get(i), marginBuffer, i /* Date index */, i > 0 ? marketValue : null, marginCall, sleepMillis1);
			// The null will result in no update for the market value plot
			sdcVisual.updateWithValue(scenarioDates.get(i), marginBuffer, i, null, 0, sleepMillis2);
		}
	}

	public void start() throws Exception {

		seriesMarketValues = new ArrayList<>();

		plotMarginAccounts = new Plot2DBarFX(null,
				"Smart Contract Balances",
				"Counterparty",
				"Balance",
				new DecimalFormat("####.00"),
				0.0,
				3.0E5,
				2.5E4, false);
		plotMarginAccounts.setIsSeriesStacked(true);

		plotMarketValue = new Plot2DFX();
		plotMarketValue.setIsLegendVisible(false);
		plotMarketValue.setTitle("Market Value");
		plotMarketValue.setXAxisLabel("Date");
		plotMarketValue.setYAxisLabel("Market Value");

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				// This method is invoked on Swing thread
				final JFrame frame = new JFrame("Smart Derivative Contract: Settlement Visualization");
				final JFXPanel fxPanel = new JFXPanel();
				frame.add(fxPanel);
				frame.setVisible(true);
				frame.setSize(1600, 600);
				//				frame.setSize(960, 540+22);

				Platform.runLater(new Runnable() {
					@Override
					public void run() {

						final FlowPane pane = new FlowPane();
						pane.getChildren().addAll(plotMarginAccounts.get(), plotMarketValue.get());
//						pane.setPrefSize(1000, 400);
						pane.setAlignment(Pos.TOP_LEFT);

						final Label title = new Label("Smart Derivative Contract Mechanics");
						title.setFont(new Font("Helvetica", 48));
						title.setPadding(new javafx.geometry.Insets(0, 0, 32, 0));
						final VBox root = new VBox();
						root.setAlignment(Pos.TOP_CENTER);
						root.getChildren().addAll(title, pane);

						final Scale scale = new Scale();
						scale.setX(1.92);
						scale.setY(1.92);
						scale.setPivotX(0);
						scale.setPivotY(0);
						pane.getTransforms().add(scale);

						final Scene scene = new Scene(root, 1920, 1080);
						scene.getStylesheets().add(VisualiserSDC.class.getResource("barchart.css").toExternalForm());
						fxPanel.setScene(scene);
					}
				});
			}
		});
	}

	void updateWithValue(final LocalDateTime date, final double base, final double x, final Double value, final double increment, long sleepMillis) throws InterruptedException {
		final List<Category2D> marginBase = new ArrayList<>();
		marginBase.add(new Category2D("Counterparty 1", base + Math.min(0, +increment)));
		marginBase.add(new Category2D("Counterparty 2", base + Math.min(0, -increment)));

		final List<Category2D> marginRemoved = new ArrayList<>();
		marginRemoved.add(new Category2D("Counterparty 1", -Math.min(0, +increment)));
		marginRemoved.add(new Category2D("Counterparty 2", -Math.min(0, -increment)));

		final List<Category2D> marginExcessed = new ArrayList<>();
		marginExcessed.add(new Category2D("Counterparty 1", Math.max(0, +increment)));
		marginExcessed.add(new Category2D("Counterparty 2", Math.max(0, -increment)));

		final List<PlotableCategories> plotables = new ArrayList<>();
		plotables.add(new PlotableCategories() {

			@Override
			public String getName() {
				return "Margin";
			}

			@Override
			public GraphStyle getStyle() {
				return new GraphStyle(new Ellipse2D.Float(-1.0f, -1.0f, 2.0f, 2.0f), new BasicStroke(1.0f), new Color(0.1f, 0.1f, 0.1f));
			}

			@Override
			public List<Category2D> getSeries() {
				return marginBase;
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
				return marginRemoved;
			}
		});
		plotables.add(new PlotableCategories() {

			@Override
			public String getName() {
				return "Receive";
			}

			@Override
			public GraphStyle getStyle() { return null; }

			@Override
			public List<Category2D> getSeries() {
				return marginExcessed;
			}
		});

		plotMarginAccounts.update(plotables);

		if (value != null) {
			final List<Plotable2D> plotables2 = new ArrayList<>();
			plotables2.add(new Plotable2D() {

				@Override
				public String getName() {
					return "Market Value";
				}

				@Override
				public GraphStyle getStyle() {
					return new GraphStyle(new Ellipse2D.Float(-3.0f, -3.0f, 6.0f, 6.0f), new BasicStroke(1.0f), new Color(1.0f, 0.0f, 0.0f));
				}

				@Override
				public List<Point2D> getSeries() {
					return seriesMarketValues;
				}
			});

			seriesMarketValues.add(new Point2D(x, value));

			plotMarketValue.update(plotables2);
			plotMarketValue.setTitle("Market Value (02.05.2008-" + date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ")");
		}

		Thread.sleep(sleepMillis);
	}
}
