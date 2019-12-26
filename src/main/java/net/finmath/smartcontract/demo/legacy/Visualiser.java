package net.finmath.smartcontract.demo.legacy;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CombinedDomainCategoryPlot;
import org.jfree.ui.ApplicationFrame;

import net.finmath.smartcontract.demo.legacy.plotgeneration.PlotGenerator;

/**
 *  Visualiser is an abstract class which handles the automatic update of an Event.
 *
 * @author Peter Kohl-Landgraf
 */

public class Visualiser extends ApplicationFrame implements ActionListener {
	private JPanel content;
	private ChartPanel chartPanel;
	private final List<PlotGenerator> chartGeneratorList;

	private final int horizontalLength=800;
	private final int verticalLenght=500;


	private final Timer timer = new Timer(200, this);

	public Visualiser(final String title, final List<PlotGenerator> chartGeneratorList) {
		super(title);
		this.chartGeneratorList=chartGeneratorList;
		this.generatePlot(null); /*Default Initialisation of Chart*/
		timer.start();
	}

	/**
	 *
	 * @param event event based plot generation
	 */
	private    void     generatePlot(final ActionEvent event){


		//		final JFreeChart chart = chartGenerator.createPlot(chartData);

		//final CategoryAxis domainAxis = new CategoryAxis("Category");
		final CombinedDomainCategoryPlot plot = new CombinedDomainCategoryPlot();
		//plot.setOrientation(PlotOrientation.VERTICAL);


		final JFreeChart chart = new JFreeChart(
				this.getTitle(),
				new Font("Arial", Font.BOLD, 12),
				plot,
				false
				);

		//Sets background color of chart
		chart.setBackgroundPaint(Color.LIGHT_GRAY);

		//Created JPanel to show graph on screen
		content = new JPanel(new FlowLayout());

		//Created Chartpanel for chart area
		chartPanel = new ChartPanel(chart);

		chartGeneratorList.stream().map(generator->generator.createPlot(event)).forEach(subplot->
		{
			final ChartPanel chartPanel2 = new ChartPanel(new JFreeChart(
					this.getTitle(),
					new Font("Arial", Font.BOLD, 12),
					subplot,
					false
					));
			chartPanel2.setPreferredSize(new java.awt.Dimension(horizontalLength, verticalLenght));
			chartPanel2.repaint();
			content.add(chartPanel2);
		}
				);

		//Added chartpanel to main panel
		//	content.add(chartPanel);

		//Sets the size of whole window (JPanel)

		//Puts the whole content on a Frame
		setContentPane(content);
		//content.repaint();
		content.updateUI();
	}

	/**
	 *
	 * @param event triggers plot update
	 */

	public void actionPerformed(final ActionEvent event)
	{
		this.generatePlot(event);
	}



}
