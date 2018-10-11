package net.finmath.smartcontract.simulation.curvecalibration;

import java.time.LocalDate;

public interface CalibrationContext {
    LocalDate getReferenceDate();

    double getAccuracy();
}
