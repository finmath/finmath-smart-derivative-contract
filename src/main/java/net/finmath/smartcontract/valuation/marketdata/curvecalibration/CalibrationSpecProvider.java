package net.finmath.smartcontract.valuation.marketdata.curvecalibration;

import net.finmath.marketdata.calibration.CalibratedCurves;

/**
 * Provides a way to get a CalibrationSpec for finmath calibration.
 *
 * @see net.finmath.marketdata.calibration.CalibratedCurves.CalibrationSpec
 */
public interface CalibrationSpecProvider {
	CalibratedCurves.CalibrationSpec getCalibrationSpec(CalibrationContext ctx);
}
