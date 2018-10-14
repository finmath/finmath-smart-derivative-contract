package net.finmath.smartcontract.simulation.curvecalibration;

import java.time.LocalTime;
import java.util.Arrays;

import net.finmath.marketdata.calibration.CalibratedCurves;
import net.finmath.marketdata.model.AnalyticModelInterface;

/**
 * Contains the result of a calibration adding additional statistics to the calibrated model.
 */
public class CalibrationResult {
	private LocalTime freshness;
	private CalibratedCurves calibration;
	private CalibratedCurves.CalibrationSpec[] calibrationSpecs;

	public CalibrationResult(CalibratedCurves c, CalibratedCurves.CalibrationSpec... specs) {
		this.calibration = c;
		this.calibrationSpecs = specs;
		this.freshness = LocalTime.now();
	}

	public AnalyticModelInterface getCalibratedModel() {
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
