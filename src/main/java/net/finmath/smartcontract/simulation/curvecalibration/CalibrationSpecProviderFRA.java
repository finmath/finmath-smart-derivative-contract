package net.finmath.smartcontract.simulation.curvecalibration;


import net.finmath.marketdata.calibration.CalibratedCurves;
import net.finmath.time.Schedule;
import net.finmath.time.ScheduleGenerator;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;

/**
 * A calibration spec provider for fras.
 *
 * @author Luca Del Re
 * @author Peter Kohl-Landgraf
 * @author Christian Fries
 */
public class CalibrationSpecProviderFRA implements CalibrationSpecProvider {
    private final String tenorLabel;
    private final String maturityLabel;
    private final double fraRate;
    private final String startOffsetLabel;

    /**
     * @param tenorLabel The tenor label of the IBOR.
     * @param maturityLabel The maturity label provided in months
     * @param fraRate The fra rate (use 0.05 for 5%).
     */
    public CalibrationSpecProviderFRA(final String tenorLabel, final String maturityLabel, final double fraRate) {
        this.tenorLabel = tenorLabel;
        this.maturityLabel = maturityLabel;
        this.fraRate = fraRate;
        this.startOffsetLabel = maturityLabel.replaceAll("[^0-9]", "") + "m";
    }

    @Override
    public CalibratedCurves.CalibrationSpec getCalibrationSpec(final CalibrationContext ctx) {
        final Schedule scheduleInterfaceRec = ScheduleGenerator.createScheduleFromConventions(ctx.getReferenceDate(), 2, startOffsetLabel, maturityLabel, "tenor", "act/360", "first", "modified_following", new BusinessdayCalendarExcludingTARGETHolidays(), 0, 0);
        final double calibrationTime = scheduleInterfaceRec.getFixing(scheduleInterfaceRec.getNumberOfPeriods() - 1);

        final String curveName = String.format("forward-EUR-%1$s", tenorLabel);

        return new CalibratedCurves.CalibrationSpec("EUR-" + tenorLabel + maturityLabel, "FRA", scheduleInterfaceRec, curveName, fraRate, "discount-EUR-OIS", null, "", 0.0, null, curveName, calibrationTime);
    }
}
