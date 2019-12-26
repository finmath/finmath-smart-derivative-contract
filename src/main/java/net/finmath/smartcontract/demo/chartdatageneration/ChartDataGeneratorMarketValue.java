package net.finmath.smartcontract.demo.chartdatageneration;

import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

import org.jfree.data.category.DefaultCategoryDataset;

import net.finmath.smartcontract.oracle.historical.ValuationOraclePlainSwapHistoricScenarios;


/**
 *  This is a very simple dataset generator which generates chart data for a time series of market vaues
 *
 * @author Peter Kohl-Landgraf
 *
 */
public class ChartDataGeneratorMarketValue implements ChartDataGenerator {

	private final ValuationOraclePlainSwapHistoricScenarios oracle;
	private final List<LocalDateTime> scenarioDates;
	private final LinkedHashMap<String,Double> dataMap;

	public ChartDataGeneratorMarketValue(final ValuationOraclePlainSwapHistoricScenarios oracle, final List<LocalDateTime> scenarioDates) {
		this.oracle = oracle;
		this.scenarioDates = scenarioDates;
		dataMap=new LinkedHashMap<>();
		dataMap.put(scenarioDates.get(0).toLocalDate().toString(),this.oracle.getValue(scenarioDates.get(0), scenarioDates.get(0)));
	}

	@Override
	public ChartData generatedChartData(final ActionEvent event){

		final DefaultCategoryDataset result = new DefaultCategoryDataset();
		final double marketValue = this.oracle.getValue(scenarioDates.get(1), scenarioDates.get(1));
		dataMap.put(scenarioDates.get(1).toLocalDate().toString(),marketValue);
		dataMap.entrySet().stream().forEach(entry->{
			result.addValue(entry.getValue(),"MarketValue",entry.getKey());
		});
		//scenarioDates.remove(0);
		return new ChartData(result);


	}
}
