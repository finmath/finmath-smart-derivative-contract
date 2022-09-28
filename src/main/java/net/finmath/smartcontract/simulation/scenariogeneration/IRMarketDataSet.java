package net.finmath.smartcontract.simulation.scenariogeneration;

import net.finmath.smartcontract.simulation.curvecalibration.CalibrationDatapoint;
import net.finmath.smartcontract.simulation.curvecalibration.CalibrationParser;
import net.finmath.smartcontract.simulation.curvecalibration.CalibrationSpecProvider;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Stream;

/**
 * IR Market Data Scenario Class holds a SecnarioDate an a Map containing CurveData
 *
 * @author Peter Kohl-Landgraf
 */

public class IRMarketDataSet {

	LocalDateTime scenarioDate;
	Map<String, IRCurveData> curveDataMap;

	//final String productKey; // = "Swap-Rate";

	public IRMarketDataSet(final Map<String, IRCurveData> curveDataMap, final LocalDateTime scenarioDate) {
		this.scenarioDate = scenarioDate;
		this.curveDataMap = curveDataMap;
		//this.productKey = productKey;
	}

	public IRCurveData getCurveData(final String curveKey) {
		return curveDataMap.get(curveKey);
	}


	/**
	 * Returns a Stream of CalibrationSpecs, curveData provided as calibration data points, will be converted to calibration specs
	 * Currently Swap-Rates, FRAS and Deposit Specs are are used.
	 *
	 * @param parser Object implementing a CalibrationParser.
	 * @return Stream of calibration spec providers.
	 */
	public Stream<CalibrationSpecProvider> getDataAsCalibrationDataProintStream(final CalibrationParser parser) {

		final Stream<CalibrationDatapoint> calibrationDatapointStream = this.curveDataMap.entrySet().stream().flatMap(curveDataEntry -> {
			final Stream<CalibrationDatapoint> calibrationDatapointSet = curveDataEntry.getValue().getDataPointStream();//.stream();//.entrySet().stream().map(entry->new CalibrationDatapoint(curveKey,entry.getKey(),entry.getValue()));//.collect(Collectors.toSet());
			return calibrationDatapointSet;
		});
		return parser.parse(calibrationDatapointStream);

	}

	public LocalDateTime getDate() {
		return scenarioDate;
	}
}
