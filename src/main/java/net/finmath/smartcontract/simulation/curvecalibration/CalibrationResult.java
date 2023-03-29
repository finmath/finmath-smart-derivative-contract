package net.finmath.smartcontract.simulation.curvecalibration;

import net.finmath.marketdata.calibration.CalibratedCurves;
import net.finmath.marketdata.model.AnalyticModel;

import java.time.LocalTime;
import java.util.Arrays;

/**
 * Contains the result of a calibration adding additional statistics to the calibrated model.
 */
public class CalibrationResult {
	private final LocalTime freshness;
	private final CalibratedCurves calibration;
	private final CalibratedCurves.CalibrationSpec[] calibrationSpecs;

	public CalibrationResult(final CalibratedCurves c, final CalibratedCurves.CalibrationSpec... specs) {
		this.calibration = c;
		this.calibrationSpecs = specs;
		this.freshness = LocalTime.now();
	}

	public AnalyticModel getCalibratedModel() {
		return calibration.getModel();
	}

	public double getSumOfSquaredErrors() {
		return Arrays.stream(calibrationSpecs).
				mapToDouble(s -> Math.pow(calibration.getCalibrationProductForSpec(s).getValue(0.0, getCalibratedModel()), 2.0)).sum();
	}

	public LocalTime getFreshness() {
		return freshness;
	}
}
