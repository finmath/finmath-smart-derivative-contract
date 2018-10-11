package net.finmath.smartcontract.simulation.curvecalibration;

import net.finmath.marketdata.calibration.CalibratedCurves;
import net.finmath.time.ScheduleGenerator;
import net.finmath.time.ScheduleInterface;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;

public class CalibrationSpecProviderOis implements CalibrationSpecProvider {
    private String maturityLabel;
    private String frequency;
    private double swapRate;

    public CalibrationSpecProviderOis(String maturityLabel, String frequency, double swapRate) {
        this.maturityLabel = maturityLabel;
        this.frequency = frequency;
        this.swapRate = swapRate;
    }

    @Override
    public CalibratedCurves.CalibrationSpec getCalibrationSpec(CalibrationContext ctx) {
        ScheduleInterface scheduleInterfaceRec = ScheduleGenerator.createScheduleFromConventions(ctx.getReferenceDate(), 2, "0D", maturityLabel, frequency, "act/360", "first", "modified_following", new BusinessdayCalendarExcludingTARGETHolidays(), 0, 1);
        ScheduleInterface scheduleInterfacePay = ScheduleGenerator.createScheduleFromConventions(ctx.getReferenceDate(), 2, "0D", maturityLabel, frequency, "act/360", "first", "modified_following", new BusinessdayCalendarExcludingTARGETHolidays(), 0, 1);
        double calibrationTime = scheduleInterfaceRec.getPayment(scheduleInterfaceRec.getNumberOfPeriods() - 1);

        return new CalibratedCurves.CalibrationSpec(String.format("EUR-OIS-%1$s", maturityLabel), "Swap", scheduleInterfaceRec, "forward-EUR-OIS", 0.0, "discount-EUR-OIS", scheduleInterfacePay, "", swapRate, "discount-EUR-OIS", "discount-EUR-OIS", calibrationTime);
    }
}
