package net.finmath.smartcontract.marketdata.curvecalibration;

import java.time.LocalDate;

/**
 * Interface for classes providing a calibration context in terms of a reference date and calibration info.
 *
 * @author Luca Del Re
 * @author Peter Kohl-Landgraf
 * @author Christian Fries
 */
public interface CalibrationContext {
	LocalDate getReferenceDate();

	double getAccuracy();
}
