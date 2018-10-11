package net.finmath.smartcontract.simulation.curvecalibration;

import net.finmath.marketdata.calibration.CalibratedCurves;

/**
 * Provides a way to get a {@see CalibrationSpec} for finmath calibration.
 */
public interface CalibrationSpecProvider {
    CalibratedCurves.CalibrationSpec getCalibrationSpec(CalibrationContext ctx);
}
