package net.finmath.smartcontract.marketdata;

import net.finmath.marketdata.calibration.CalibratedCurves;
import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.model.AnalyticModelFromCurvesAndVols;
import net.finmath.marketdata.model.curves.*;
import net.finmath.marketdata.products.Deposit;
import net.finmath.marketdata.products.ForwardRateAgreement;
import net.finmath.marketdata.products.Swap;
import net.finmath.marketdata.products.SwapLeg;
import net.finmath.modelling.DescribedProduct;
import net.finmath.modelling.ProductDescriptor;
import net.finmath.modelling.descriptor.InterestRateSwapLegProductDescriptor;
import net.finmath.modelling.descriptor.InterestRateSwapProductDescriptor;
import net.finmath.modelling.descriptor.xmlparser.FPMLParser;
import net.finmath.modelling.productfactory.InterestRateAnalyticProductFactory;
import net.finmath.smartcontract.marketdata.curvecalibration.*;
import net.finmath.smartcontract.marketdata.curvecalibration.CalibrationDataset;
import net.finmath.smartcontract.oracle.interestrates.ValuationOraclePlainSwap;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import net.finmath.time.Period;
import net.finmath.time.Schedule;
import net.finmath.time.ScheduleGenerator;
import net.finmath.time.businessdaycalendar.BusinessdayCalendar;
import net.finmath.time.businessdaycalendar.BusinessdayCalendarExcludingTARGETHolidays;
import org.junit.jupiter.api.*;
import org.springframework.cglib.core.Local;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MarketDataPastFixingsTests {

    final  LocalDate referenceDate = LocalDate.of(2023,1,30);
    final CalibrationContext ctx = new CalibrationContextImpl(referenceDate, 1.0E-9);
    final LocalDate tradeDate = LocalDate.of(2022,12,15);
    final String MaturityKey = "10Y";
    final double fixRate = 0.0264;
    final double[] pastFixingArray = {0.02493};
    final String forward6MCurveKey = "forward-EUR-6M";
    final String discountCurveKey = "discount-EUR-OIS";
    final String forwardOISCurveKey = "forward-EUR-OIS";
    Schedule scheduleFloat;

    Swap swapFromParams;
    Swap swapFromXML;
    List<CalibrationDataset> scenarioList;
    List<CalibrationDataset> scenarioListwFixings;
    CalibratedCurves.CalibrationSpec[] calibrationSpecs;
    ForwardCurve fixedCurve;
    AnalyticModel modelWithPastFixings;

    @BeforeAll
    void initCalibrationSpecs() throws Exception{
        /* Retrieve and transform into calibration items
//        Path path = Path.of("C:\\Temp\\finmath-smart-derivative-contract-MarketData\\src\\main\\resources\\net.finmath.smartcontract.client\\md_testset1.json");
        final String jsonStr = new String(MarketDataImportTest.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.client/md_testset1.json").readAllBytes(), StandardCharsets.UTF_8);
        final String jsonStr2 = new String(MarketDataImportTest.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.client/md_testset_with_fixings.json").readAllBytes(), StandardCharsets.UTF_8);
        scenarioList = CalibrationParserDataItems.getScenariosFromJsonString(jsonStr);
        scenarioListwFixings = CalibrationParserDataItems.getScenariosFromJsonString(jsonStr2);
        String json = scenarioListwFixings.get(0).serializeToJson();
        CalibrationParser parser = new CalibrationParserDataItems();
        Stream<CalibrationSpecProvider> specProviderStream = scenarioList.get(0).getDataAsCalibrationDataPointStream(parser);
        calibrationSpecs = specProviderStream.map(c -> c.getCalibrationSpec(ctx)).toArray(CalibratedCurves.CalibrationSpec[]::new);
        */
    }

    @BeforeAll
    void initSwapProducts() throws Exception{
        /*
        scheduleFloat = ScheduleGenerator.createScheduleFromConventions(referenceDate,tradeDate,2, "0D",MaturityKey , "semiannual", "act/360", "first", "modfollow", new BusinessdayCalendarExcludingTARGETHolidays(), -2, 0);
        final Schedule scheduleFix = ScheduleGenerator.createScheduleFromConventions(referenceDate,tradeDate, 2, "0D", MaturityKey, "annual", "E30/360", "first", "modfollow", new BusinessdayCalendarExcludingTARGETHolidays(), -2, 0);
        final SwapLeg floatLeg = new SwapLeg(scheduleFloat,forward6MCurveKey,0.0,discountCurveKey);//new SwapLeg(Optional.of(LocalDateTime.of(startDate, LocalTime.of(0, 0))), scheduleFloat, forwardCurveName, 0.0, discountCurveName);
        final SwapLeg fixLeg = new SwapLeg(scheduleFix,"",fixRate,discountCurveKey);
        swapFromParams = new Swap(fixLeg,floatLeg);

        String sdcXML = new String(MarketDataPastFixingsTests.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/sdc2.xml").readAllBytes(), StandardCharsets.UTF_8);
        SmartDerivativeContractDescriptor productDescriptor = SDCXMLParser.parse(sdcXML);
        String ownerPartyID = productDescriptor.getUnderlyingReceiverPartyID();
        InterestRateSwapProductDescriptor underlying = (InterestRateSwapProductDescriptor)new FPMLParser(ownerPartyID, "forward-EUR-6M", "discount-EUR-OIS").getProductDescriptor(productDescriptor.getUnderlying());
        InterestRateSwapLegProductDescriptor legReceiver = (InterestRateSwapLegProductDescriptor) underlying.getLegReceiver();
        InterestRateSwapLegProductDescriptor legPayer = (InterestRateSwapLegProductDescriptor) underlying.getLegPayer();
        InterestRateAnalyticProductFactory productFactory = new InterestRateAnalyticProductFactory(referenceDate);
        DescribedProduct<? extends ProductDescriptor> legReceiverProduct = productFactory.getProductFromDescriptor(legReceiver);
        DescribedProduct<? extends ProductDescriptor> legPayerProduct = productFactory.getProductFromDescriptor(legPayer);

        swapFromXML = new Swap((SwapLeg) legReceiverProduct, (SwapLeg) legPayerProduct);

        double[] pastFixingTime = {scheduleFloat.getFixing(0)};
        double paymentOffset = scheduleFloat.getPayment(0);
        fixedCurve = ForwardCurveInterpolation.createForwardCurveFromForwards("fixed",pastFixingTime,pastFixingArray,paymentOffset);
        */
    }

    @Disabled("")
    @Test
    void testValuationOraclePlainSwap() throws Exception{
        double notional = 1.0E6;
        ValuationOraclePlainSwap valuationOraclePlainSwap = new ValuationOraclePlainSwap(swapFromXML,notional,scenarioListwFixings);

        double value = valuationOraclePlainSwap.getValue(referenceDate.atTime(17,00),LocalDateTime.of(2023,1,31,14,35, 23)).doubleValue();
        System.out.println(value);

    }

    @Disabled("")
    @BeforeAll
    void initModelWithFixedPart() throws Exception{
        /*
        /*Define and calibrate OIS and 6M Curves*/
        /*
        Curve discountOISCurve =  DiscountCurveInterpolation.createDiscountCurveFromDiscountFactors(discountCurveKey, ctx.getReferenceDate(), new double[]{0.0}, new double[]{1.0}, new boolean[]{false}, CurveInterpolation.InterpolationMethod.LINEAR, CurveInterpolation.ExtrapolationMethod.CONSTANT, CurveInterpolation.InterpolationEntity.VALUE);
        ForwardCurve forwardOISCurve = new ForwardCurveFromDiscountCurve(forwardOISCurveKey, discountCurveKey, ctx.getReferenceDate(), "3M");
        ForwardCurve forward6MCurve =  new ForwardCurveInterpolation(forward6MCurveKey, ctx.getReferenceDate(), "6M", new BusinessdayCalendarExcludingTARGETHolidays(), BusinessdayCalendar.DateRollConvention.FOLLOWING, CurveInterpolation.InterpolationMethod.LINEAR, CurveInterpolation.ExtrapolationMethod.CONSTANT, CurveInterpolation.InterpolationEntity.VALUE, ForwardCurveInterpolation.InterpolationEntityForward.FORWARD, discountCurveKey);
        Curve[] curves = {discountOISCurve,forwardOISCurve,forward6MCurve};
        final AnalyticModelFromCurvesAndVols model = new AnalyticModelFromCurvesAndVols(curves);*/
        /* Calibrate */
        /*
        Optional<CalibrationResult> optional = Optional.of(new CalibrationResult(new CalibratedCurves(calibrationSpecs, model, ctx.getAccuracy()), calibrationSpecs));
        AnalyticModel calibratedModel = optional.get().getCalibratedModel();*/

        /* Build Lego Curve: Past Part and Forward Part */
        /*
        Curve forwardCurveWithFixings = new ForwardCurveWithFixings(calibratedModel.getForwardCurve(forward6MCurveKey),fixedCurve,-0.5,0.0);
        Curve[] finalCurves = {calibratedModel.getDiscountCurve(discountCurveKey),calibratedModel.getForwardCurve(forwardOISCurveKey),forwardCurveWithFixings};
        modelWithPastFixings = new AnalyticModelFromCurvesAndVols(referenceDate,finalCurves);*/
    }

    @Disabled("")
    @Test
    void testModelWithPastFixing()  throws Exception {
        ForwardCurve curve = modelWithPastFixings.getForwardCurve(this.forward6MCurveKey);
        double pastFixingTime = scheduleFloat.getFixing(0);
        double pastRate = curve.getValue(pastFixingTime);
        Assertions.assertEquals(pastRate, pastFixingArray[0]);
    }


    @Disabled("Disabled since spot is somehow miscalibrated")
    @Test
    void testSpotRate() {
        double EUIRBOR6M = modelWithPastFixings.getForwardCurve(forward6MCurveKey).getValue(0.0);
        final Schedule scheduleInterfaceRec = ScheduleGenerator.createScheduleFromConventions(ctx.getReferenceDate(), 2, "0D", "6M", "tenor", "act/360", "first", "modified_following", new BusinessdayCalendarExcludingTARGETHolidays(), 0, 0);
        Deposit deposit = new Deposit(scheduleInterfaceRec, 0.0, discountCurveKey);
        double rateDep = deposit.getRate(modelWithPastFixings);
        Assertions.assertEquals(EUIRBOR6M, rateDep);
    }

    @Disabled("")
    @Test
    void testCalibrationOnFRA() {
        ArrayList<Double> deviations =  new ArrayList<>();
        String[] fixingOffsetLables = {"1M", "2M", "3M", "4M", "6M", "9M", "12M"};
        for (int iFixing = 0; iFixing < fixingOffsetLables.length; iFixing++) {
            String offsetLabel = fixingOffsetLables[iFixing];
            int nMonths = Integer.parseInt(offsetLabel.replace("M", ""));
            Schedule scheduleFRA = ScheduleGenerator.createScheduleFromConventions(referenceDate, referenceDate, 2, offsetLabel, "6M", "tenor", "act/360", "first", "modfollow", new BusinessdayCalendarExcludingTARGETHolidays(), -2, 0);
            ForwardRateAgreement fra = new ForwardRateAgreement(scheduleFRA, 0.0, forward6MCurveKey, discountCurveKey);
            double rate = fra.getRate(modelWithPastFixings);
            ForwardCurve forwardCurve= modelWithPastFixings.getForwardCurve(forward6MCurveKey);
            double forwardRate = forwardCurve.getForward(modelWithPastFixings, ((double) nMonths) / 12, 0.5);
            double dev = Math.abs(rate - forwardRate);
            deviations.add(dev);
//            System.out.println(offsetLabel + ": FRA-Rate - Rate from Forward: " + (rate - forwardRate));
        }
        double max = deviations.stream().max(Comparator.naturalOrder()).get();
        Assertions.assertTrue( max < 5.0E-4,"Deviation is larger than tolerance " + max );
    }

    @Disabled("")
    @Test
    void testCalibrationOnSwaps() {
        ArrayList<Double> npvs =  new ArrayList<>();
        String[] maturityLables = {"2Y","3Y","5Y","8Y","10Y","12Y","13Y","14Y","15Y","16Y","17Y","18Y","20Y","25Y","30Y","40Y"};
        for (int iFixing=0;iFixing<maturityLables.length;iFixing++) {
            String matLabel = maturityLables[iFixing];
            final Schedule scheduleInterfaceReceiv = ScheduleGenerator.createScheduleFromConventions(ctx.getReferenceDate(), 2, "0D", matLabel, "semiannual", "act/360", "first", "modified_following", new BusinessdayCalendarExcludingTARGETHolidays(), -2, 0);
            final Schedule scheduleInterfacePay = ScheduleGenerator.createScheduleFromConventions(ctx.getReferenceDate(), 2, "0D", matLabel, "annual", "E30/360", "first", "modified_following", new BusinessdayCalendarExcludingTARGETHolidays(), -2, 0);
            double swapRate = scenarioList.get(0).getDataPoints().stream()
                    .filter(datapoint->datapoint.getSpec().getCurveName().equals("Euribor6M") &&
                            datapoint.getSpec().getProductName().equals("Swap-Rate") &&
                            datapoint.getSpec().getMaturity().equals(matLabel)).mapToDouble(e -> e.getQuote()).findAny().getAsDouble();
            final SwapLeg floatLeg1 = new SwapLeg(scheduleInterfaceReceiv,forward6MCurveKey,0.0,discountCurveKey);
            final SwapLeg fixLeg2 = new SwapLeg(scheduleInterfacePay,"",swapRate,discountCurveKey);
            final Swap benchmarkSwap = new Swap(fixLeg2,floatLeg1);
            double value = benchmarkSwap.getValue(modelWithPastFixings);
            npvs.add(value);
           // System.out.println(matLabel + ": SwapNPV: " + value);
        }
        double max = npvs.stream().max(Comparator.naturalOrder()).get();
        Assertions.assertTrue( max < 1.0E-3, "Deviation is larger than tolerance " + max);
    }


    @Disabled("")
    @Test
    void testStoreDataOnFixing(){
        double fixingValue = 0.025;
        InterestRateSwapLegProductDescriptor legReceiver = (InterestRateSwapLegProductDescriptor) swapFromXML.getLegReceiver();
        Period firstPeriod = legReceiver.getLegScheduleDescriptor().getPeriods().get(0);
        LocalDate fixingDate = firstPeriod.getFixing();
        if (fixingDate.isEqual(LocalDate.now())){

        }

        // RATHER STORE all Fixings on a time period - e.g. OIS 6 M Fixings need to be stored


    }
}

