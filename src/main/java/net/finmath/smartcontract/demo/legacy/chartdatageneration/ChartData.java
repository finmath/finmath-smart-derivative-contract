package net.finmath.smartcontract.demo.legacy.chartdatageneration;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.Dataset;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A Class containing data (CategoryDataset) as well as other chart properties (Colors, Titles, Axes...)
 *
 * @author Peter Kohl-Landgraf
 */
public class ChartData {

	public enum propertyKey {
		colorListStackedBar,
		chartTitle,
		axisRange
	}

	private final Dataset dataset;
	private final Map<propertyKey, Object> chartPropertyMap;

	public ChartData(final CategoryDataset dataset) {
		this.chartPropertyMap = new HashMap<>();
		this.dataset = dataset;
	}

	public Dataset getDataset() {
		return this.dataset;
	}

	public Map getPropertyMap() {
		return chartPropertyMap;
	}

	public ChartData addProperty(final propertyKey key, final Object value) {
		this.chartPropertyMap.put(key, value);
		return this;
	}

	public ChartData addPropertyChartTitle(final String title) {
		this.chartPropertyMap.put(propertyKey.chartTitle, title);
		return this;
	}

	public List<Color> getPropertyColorListStackedBar() {
		return (List<Color>) this.chartPropertyMap.get(propertyKey.colorListStackedBar);
	}

	public String getPropertyChartTitle() {
		return (String) this.chartPropertyMap.get(propertyKey.chartTitle);
	}
}
