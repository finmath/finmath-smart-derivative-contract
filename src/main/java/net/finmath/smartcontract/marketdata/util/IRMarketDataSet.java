package net.finmath.smartcontract.marketdata.util;

import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationParser;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationSpecProvider;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * IR Market Data Scenario Class holds a SecnarioDate an a Map containing CurveData
 *
 * @author Peter Kohl-Landgraf
 */
public class IRMarketDataSet {

	LocalDateTime scenarioDate;
	Set<CalibrationDataItem> curveDataPointSet;

	public IRMarketDataSet(final Set<CalibrationDataItem> curveDataPointSet, final LocalDateTime scenarioDate) {
		this.scenarioDate = scenarioDate;
		this.curveDataPointSet = curveDataPointSet;

	}

	public 	IRMarketDataSet getScaled(double scaleFactor){

		Set<CalibrationDataItem> scaledSet = curveDataPointSet.stream().map(point->point.getClonedScaled(scaleFactor)).collect(Collectors.toSet());
		return new IRMarketDataSet(scaledSet, scenarioDate);
	}



	/**
	 * Returns a Stream of CalibrationSpecs, curveData provided as calibration data points, will be converted to calibration specs
	 * Currently Swap-Rates, FRAS and Deposit Specs are are used.
	 *
	 * @param parser Object implementing a CalibrationParser.
	 * @return Stream of calibration spec providers.
	 */
	public Stream<CalibrationSpecProvider> getDataAsCalibrationDataPointStream(final CalibrationParser parser) {

		return parser.parse(curveDataPointSet.stream());

	}

	public Set<CalibrationDataItem> getDataPoints(){
		return this.curveDataPointSet;
	}

	public LocalDateTime getDate() {
		return scenarioDate;
	}
}
