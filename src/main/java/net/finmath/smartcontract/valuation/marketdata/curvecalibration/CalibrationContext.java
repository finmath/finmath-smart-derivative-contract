package net.finmath.smartcontract.valuation.marketdata.curvecalibration;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Interface for classes providing a calibration context in terms of a reference date and calibration info.
 *
 * @author Luca Del Re
 * @author Peter Kohl-Landgraf
 * @author Christian Fries
 */
// TODO replace LocalDateTime with ZonedDateTime -> current Problem net.finmath.time.FloatingpointDate getFloatingPointDateFromDate uses LocalDateTime
public interface CalibrationContext {

	LocalDate getReferenceDate();

	LocalDateTime getReferenceDateTime();

	double getAccuracy();
}
