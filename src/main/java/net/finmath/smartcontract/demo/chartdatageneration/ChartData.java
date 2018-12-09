package net.finmath.smartcontract.demo.chartdatageneration;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.Dataset;


/**
 *  A Class containing data (CategoryDataset) as well as other chart properties (Colors, Titles, Axes...)
 *
 * @author Peter Kohl-Landgraf
 */
public class ChartData {

	public enum propertyKey {
		colorListStackedBar,
		chartTitle,
		axisRange
	}

	private Dataset dataset;
	private Map<propertyKey,Object>  chartPropertyMap;

	public ChartData(CategoryDataset dataset) {
		this.chartPropertyMap = new HashMap<>();
		this.dataset = dataset;
	}

	public Dataset    getDataset(){
		return this.dataset;
	}

	public Map     getPropertyMap(){
		return chartPropertyMap;
	}

	public ChartData addProperty(propertyKey key, Object value){
		this.chartPropertyMap.put(key,value);
		return this;
	}

	public ChartData addPropertyChartTitle(String title){
		this.chartPropertyMap.put(propertyKey.chartTitle,title);
		return this;
	}

	public List<Color>     getPropertyColorListStackedBar(){
		return (List<Color>) this.chartPropertyMap.get(propertyKey.colorListStackedBar);
	}

	public String     getPropertyChartTitle(){
		return (String) this.chartPropertyMap.get(propertyKey.chartTitle);
	}
}
