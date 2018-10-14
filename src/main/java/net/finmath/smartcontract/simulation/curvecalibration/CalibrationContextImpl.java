package net.finmath.smartcontract.simulation.curvecalibration;

import java.time.LocalDate;

/**
 * A calibration context in terms of a reference date and calibration info.
 *
 * @author Luca Del Re
 * @author Peter Kohl-Landgraf
 * @author Christian Fries
 */
public class CalibrationContextImpl implements CalibrationContext {
	private LocalDate referenceDate;
	private double accuracy;

	public CalibrationContextImpl(LocalDate referenceDate, double accuracy) {
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
