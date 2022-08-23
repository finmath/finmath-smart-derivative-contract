package net.finmath.smartcontract.simulation.scenariogeneration;

import net.finmath.smartcontract.simulation.curvecalibration.CalibrationDatapoint;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * A IRCurveData Class holds  a Set of CalibrationDataPoints and its own curve key
 *
 * @author Peter Kohl-Landgraf
 */
public class IRCurveData {

	public Set<CalibrationDatapoint> curveDataPointSet;
	private final String curveKey;

	/**
	 * CurveFromInterpolationPoints Key and CurveDAtaPointSet to be provided
	 *
	 * @param curveKey Key identifying the curve.
	 * @param datapoints Map from product type to a map of maturities to calibration data.
	 */
	public IRCurveData(final String curveKey, final Set<CalibrationDatapoint> datapoints){
		this.curveKey = curveKey;
		curveDataPointSet = datapoints;
	}

	/**
	 * CurveFromInterpolationPoints Key and Map will be provided. Map maps Each productType (e.g. FRA/SWAP) to Map of MaturityKeys and Rates)
	 *
	 * @param curveKey Key identifying the curve.
	 * @param typeCurveMap Map from product type to a map of maturities to calibration data.
	 */
	public IRCurveData(final String curveKey, final Map<String,Map<String,Double>> typeCurveMap){
		this.curveKey = curveKey;
		curveDataPointSet = typeCurveMap.entrySet().stream().flatMap(entry->entry.getValue().entrySet().stream().map(
				curvePointEntry->new CalibrationDatapoint(curveKey,entry.getKey(),curvePointEntry.getKey(),curvePointEntry.getValue()))).collect(Collectors.toSet());
	}


	/**
	 * Returns Stream of calibration data points for a given product type
	 *
	 * @param productType String identifying the product type.
	 * @return Stream of calibration data points.
	 */
	public Stream<CalibrationDatapoint> getDataPointStreamForProductType(final String productType){
		return curveDataPointSet.stream().filter(dataPoint->dataPoint.getProductName().equals(productType));
	}

	public Stream<CalibrationDatapoint> getDataPointStream(){
		return curveDataPointSet.stream();
	}


}
