package net.finmath.smartcontract.valuation.marketdata.curvecalibration;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * A calibration context in terms of a reference date and calibration info.
 *
 * @author Luca Del Re
 * @author Peter Kohl-Landgraf
 * @author Christian Fries
 */
public class CalibrationContextImpl implements CalibrationContext {
	private final LocalDateTime referenceDateTime;
	private final double accuracy;

	public CalibrationContextImpl(final LocalDateTime referenceDateTime, final double accuracy) {
		this.referenceDateTime = referenceDateTime;
		this.accuracy = accuracy;
	}

	@Override
	public LocalDate getReferenceDate() {
		return referenceDateTime.toLocalDate();
	}

	@Override
	public LocalDateTime getReferenceDateTime() {
		return referenceDateTime;
	}

	@Override
	public double getAccuracy() {
		return accuracy;
	}
}
