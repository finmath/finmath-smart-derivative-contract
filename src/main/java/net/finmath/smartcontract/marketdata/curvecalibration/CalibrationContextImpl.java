package net.finmath.smartcontract.marketdata.curvecalibration;

import java.time.LocalDate;

/**
 * A calibration context in terms of a reference date and calibration info.
 *
 * @author Luca Del Re
 * @author Peter Kohl-Landgraf
 * @author Christian Fries
 */
public class CalibrationContextImpl implements CalibrationContext {
	private final LocalDate referenceDate;
	private final double accuracy;

	public CalibrationContextImpl(final LocalDate referenceDate, final double accuracy) {
		this.referenceDate = referenceDate;
		this.accuracy = accuracy;
	}

	@Override
	public LocalDate getReferenceDate() {
		return referenceDate;
	}

	@Override
	public double getAccuracy() {
		return accuracy;
	}
}
