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
    private String curveKey;


    /**
     * Curve Key and Map will be provided. Map maps Each productType (e.g. FRA/SWAP) to Map of MaturityKeys and Rates)
     *
     */
    public IRCurveData(String curveKey, Map<String,Map<String,Double>> typeCurveMap){
        this.curveKey = curveKey;
        curveDataPointSet = typeCurveMap.entrySet().stream().flatMap(entry->entry.getValue().entrySet().stream().map(
                curvePointEntry->new CalibrationDatapoint(curveKey,entry.getKey(),curvePointEntry.getKey(),curvePointEntry.getValue()))).collect(Collectors.toSet());
    }


    /**
     * Returns Stream of calibration data points for a given product type
     *
     */
    public Stream<CalibrationDatapoint> getDataPointStreamForProductType(String productType){
        return curveDataPointSet.stream().filter(dataPoint->dataPoint.getProductName().equals(productType));
    }


}
