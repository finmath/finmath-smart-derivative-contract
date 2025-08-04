package net.finmath.smartcontract.valuation.implementation;

import net.finmath.marketdata.model.AnalyticModel;
import net.finmath.marketdata.model.curves.DiscountCurve;
import net.finmath.marketdata.products.AnalyticProduct;
import net.finmath.modelling.descriptor.InterestRateSwapLegProductDescriptor;
import net.finmath.modelling.descriptor.InterestRateSwapProductDescriptor;
import net.finmath.modelling.descriptor.ScheduleDescriptor;
import net.finmath.modelling.descriptor.xmlparser.FPMLParser;
import net.finmath.modelling.productfactory.InterestRateAnalyticProductFactory;
import net.finmath.smartcontract.model.ExceptionId;
import net.finmath.smartcontract.model.SDCException;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.flowschedule.FlowScheduleSwap;
import net.finmath.smartcontract.product.flowschedule.FlowScheduleSwapLeg;
import net.finmath.smartcontract.product.flowschedule.FlowScheduleSwapLegPeriod;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import net.finmath.smartcontract.product.xml.Smartderivativecontract;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.*;
import net.finmath.time.FloatingpointDate;
import net.finmath.time.Period;
import net.finmath.time.Schedule;
import net.finmath.time.ScheduleFromPeriods;
import net.finmath.time.daycount.DayCountConvention;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;


/**
 *  Decomposes the underlying swap of a Smart Derivative Contract contained in a FMPL
 *  into individual periods and values them using historic market data.
 *  Stores the results as flow-schedule objects of type {@link FlowScheduleSwap} or {@link FlowScheduleSwapLeg}.
 *
 * @author Raphael Prandtl
 */
public class FlowScheduleCalculator {

	public enum LegType {
		LEG_RECEIVER("legReceiver"),
		LEG_PAYER("legPayer");

		private final String value;

		LegType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	private static final String FIXING = "Fixing";
	private static final String DEPOSIT = "Deposit";

	private ConcurrentHashMap<String, SmartDerivativeContractDescriptor> productMap = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, ConcurrentHashMap<String, AnalyticModel>>  modelMap = new ConcurrentHashMap<>();

	public FlowScheduleCalculator() {}

	/**
	 *
	 * @param productData The FPML of the Smart Derivative Contract
	 * @param marketData The FPML of the historic market data
	 * @return The flow schedule of the underlying swap as an XML-string.
	 */
	public String getFlowScheduleSwapXml(String productData, String marketData) {
		FlowScheduleSwap flowScheduleSwap = getFlowScheduleSwap(productData, marketData);
		String flowScheduleSwapXml = SDCXMLParser.marshalClassToXMLString(flowScheduleSwap);
		return flowScheduleSwapXml;
	}

	/**
	 *
	 * @param productData The FPML of the Smart Derivative Contract
	 * @param marketData The FPML of the historic market data
	 * @return The flow schedule of the underlying swap as a {@link FlowScheduleSwap}
	 */
	public FlowScheduleSwap getFlowScheduleSwap(String productData, String marketData) {
		FlowScheduleSwap flowScheduleSwap = createFlowScheduleSwap(productData, marketData);
		return flowScheduleSwap;
	}

	/*
	 Creates the flow schedule for each swap leg, currently only implemented for plain-vanilla-swaps with one receiver and one payer leg
	 and combines them to a FlowScheduleSwap object.
	 */
	private FlowScheduleSwap createFlowScheduleSwap(String productData, String marketData) {
		SmartDerivativeContractDescriptor productDescriptor = getSmartderivativeContractDescriptor(productData);
		Smartderivativecontract smartderivativecontract = SDCXMLParser.unmarshalXml(productData, Smartderivativecontract.class);
		List<FlowScheduleSwapLeg> flowScheduleSwapLegs = getFlowScheduleSwapLegs(productData, marketData);

		FlowScheduleSwap flowScheduleSwap = new FlowScheduleSwap();
		flowScheduleSwap.setDltTradeId(productDescriptor.getDltAddress());
		flowScheduleSwap.setDltAddress(productDescriptor.getDltAddress());
		flowScheduleSwap.setUniqueTradeIdentifier(productDescriptor.getUniqueTradeIdentifier());
		flowScheduleSwap.setSettlementCurrency(productDescriptor.getCurrency());
		flowScheduleSwap.setTradeType(productDescriptor.getTradeType());
		flowScheduleSwap.setParties(smartderivativecontract.getParties());
		flowScheduleSwap.setReceiverPartyID(productDescriptor.getUnderlyingReceiverPartyID());
		flowScheduleSwap.setFlowScheduleSwapLegs(flowScheduleSwapLegs);

		return flowScheduleSwap;
	}

	private List<FlowScheduleSwapLeg> getFlowScheduleSwapLegs(String productData, String marketData) {
		List<FlowScheduleSwapLeg> flowScheduleSwapLegs = new ArrayList<>();
		flowScheduleSwapLegs.add(getFlowScheduleSwapLeg(productData, marketData, LegType.LEG_RECEIVER));
		flowScheduleSwapLegs.add(getFlowScheduleSwapLeg(productData, marketData, LegType.LEG_PAYER));
		return flowScheduleSwapLegs;
	}

	/**
	 *
	 * @param productData The FPML of the Smart Derivative Contract
	 * @param marketData The FPML of the historic market data
	 * @param legType The {@link FlowScheduleCalculator.LegType} of the swap, i.e. {@code LEG_RECEIVER} or {@code LEG_PAYER}
	 * @return The flow schedule of the underlying swap leg as an XML-string
	 */
	public FlowScheduleSwapLeg getFlowScheduleSwapLeg(String productData, String marketData, LegType legType) {
		FlowScheduleSwapLeg flowScheduleSwapLeg = createFlowScheduleSwapLeg(productData, marketData, legType);
		return flowScheduleSwapLeg;
	}

	// Creates the flow schedule for each period of a swap leg and combines them to a FlowScheduleSwapLeg
	private FlowScheduleSwapLeg createFlowScheduleSwapLeg(String productData, String marketData, LegType legType) {
		SmartDerivativeContractDescriptor productDescriptor = getSmartderivativeContractDescriptor(productData);
		AnalyticModel calibratedModel = getCalibratedModel(productData, marketData);
		InterestRateSwapLegProductDescriptor swapLegProductDescriptor = getInterestRateSwapLegProductDescriptor(productData, legType);

		List<FlowScheduleSwapLegPeriod> flowScheduleSwapLegPeriods = getFlowScheduleSwapLegPeriods(swapLegProductDescriptor, legType, calibratedModel, productDescriptor.getTradeDate());
		FlowScheduleSwapLeg flowScheduleSwapLeg = new FlowScheduleSwapLeg();
		flowScheduleSwapLeg.setLegType(legType.getValue());
		flowScheduleSwapLeg.setFlowScheduleSwapLegPeriods(flowScheduleSwapLegPeriods);
		return flowScheduleSwapLeg;
	}

	/*
	Decomposes a swap leg with N periods into N single period swaps and values them separately.
	Stores the rolled-out period and flow amounts of each single period swap as a FlowScheduleSwapLegPeriod
	 */
	private List<FlowScheduleSwapLegPeriod> getFlowScheduleSwapLegPeriods(InterestRateSwapLegProductDescriptor swapLegDescriptor, LegType legType, AnalyticModel model, LocalDate productReferenceDate) {
		ScheduleFromPeriods scheduleFromPeriods = (ScheduleFromPeriods) swapLegDescriptor.getLegScheduleDescriptor().getSchedule(productReferenceDate);
		InterestRateAnalyticProductFactory productFactory = new InterestRateAnalyticProductFactory(productReferenceDate);

		final String forwardCurveName 				= swapLegDescriptor.getForwardCurveName();
		final String discountCurveName 				= swapLegDescriptor.getDiscountCurveName();
		final double[] notionals 					= swapLegDescriptor.getNotionals();
		final double[] spreads 						= swapLegDescriptor.getSpreads();
		final boolean isNotionalExchanged 			= swapLegDescriptor.isNotionalExchanged();
		// Required to adjust the retrieval time of the discount factors for the relative distance between trade and market date
		final double productToModelTimeOffset 		= FloatingpointDate.getFloatingPointDateFromDate(model.getCurve(discountCurveName).getReferenceDate(), productReferenceDate);
		final DayCountConvention dayCountConvention = scheduleFromPeriods.getDaycountconvention();

		List<FlowScheduleSwapLegPeriod> flowScheduleSwapLegPeriods = new ArrayList<>(scheduleFromPeriods.getNumberOfPeriods());
		for (int i = 0; i < scheduleFromPeriods.getNumberOfPeriods(); i++) {
			Period period = scheduleFromPeriods.getPeriod(i);
			ScheduleDescriptor scheduleDescriptor = new ScheduleDescriptor(List.of(period), dayCountConvention);
			Schedule schedule = scheduleDescriptor.getSchedule(productReferenceDate);

			InterestRateSwapLegProductDescriptor singlePeriodSwapLeg = new InterestRateSwapLegProductDescriptor(forwardCurveName, discountCurveName, scheduleDescriptor, notionals[i], spreads[i], isNotionalExchanged);
			AnalyticProduct product = (AnalyticProduct) productFactory.getProductFromDescriptor(singlePeriodSwapLeg);
			DiscountCurve discountCurve = model.getDiscountCurve(discountCurveName);

			double flowAmount 		= product.getValue(0.0, model); // Flow schedule is always calculated as of marketDataTime = 0.0
			double periodLength   	= schedule.getPeriodLength(0); // always periodIndex 0 for single period swap leg
			double discountFactor 	= discountCurve.getDiscountFactor(scheduleFromPeriods.getPayment(i) + productToModelTimeOffset); // payment time relative to marketDataTime
			double rate 			= flowAmount / discountFactor / periodLength / notionals[i]; // "reverse" swap leg getValue() for each period
			if (legType.equals(LegType.LEG_PAYER)) {
				flowAmount = (-1.0) * flowAmount;
			}
			flowScheduleSwapLegPeriods.add(createFlowScheduleSwapLegPeriod(period, notionals[i], flowAmount, rate));
		}
		return flowScheduleSwapLegPeriods;
	}

	private FlowScheduleSwapLegPeriod createFlowScheduleSwapLegPeriod(Period period, double notional, double flowAmount, double rate) {
		FlowScheduleSwapLegPeriod flowScheduleSwapLegPeriod = new FlowScheduleSwapLegPeriod();
		// TODO Replace atStartOfDay with correct settlementTime
		flowScheduleSwapLegPeriod.setFixingDate(period.getFixing().atStartOfDay());
		flowScheduleSwapLegPeriod.setPeriodStartDate(period.getPeriodStart().atStartOfDay());
		flowScheduleSwapLegPeriod.setPeriodEndDate(period.getPeriodEnd().atStartOfDay());
		flowScheduleSwapLegPeriod.setPaymentDate(period.getPayment().atStartOfDay());
		flowScheduleSwapLegPeriod.setNotional(notional);
		flowScheduleSwapLegPeriod.setRate(rate);
		flowScheduleSwapLegPeriod.setFlowAmount(flowAmount);

		return flowScheduleSwapLegPeriod;
	}

	// On the first call or when the productData has changed, we need to reparse the productDescriptor.
	public SmartDerivativeContractDescriptor getSmartderivativeContractDescriptor(String productData) {
		return productMap.computeIfAbsent(productData, k -> {
			try {
				return SDCXMLParser.parse(productData);
			} catch (Exception e) {
				throw new SDCException(ExceptionId.SDC_XML_PARSE_ERROR, e.getMessage());
			}
		});
	}

	// If the productData or marketData has changed we need to recalibrate the model
	private AnalyticModel getCalibratedModel(String productData, String marketData) {
		ConcurrentHashMap<String, AnalyticModel> productDataToModelMap = modelMap.computeIfAbsent(marketData, k -> new ConcurrentHashMap<>());
		return productDataToModelMap.computeIfAbsent(productData, k -> calibrateModel(productData, marketData));
	}

	private AnalyticModel calibrateModel(String productData, String marketData) {
		SmartDerivativeContractDescriptor productDescriptor = getSmartderivativeContractDescriptor(productData);
		CalibrationDataset calibrationDataset = CalibrationParserDataItems.getCalibrationDataSetFromXML(marketData, productDescriptor.getMarketdataItemList());
		// Add most recent published overnight rate as proxy for discounting from t=0 to t+1
		addOvernightDepositRate(calibrationDataset);
		LocalDateTime marketDataTime = calibrationDataset.getDate();
		final CalibrationParserDataItems parser = new CalibrationParserDataItems();
		try {
			final Stream<CalibrationSpecProvider> calibrationItems = calibrationDataset.getDataAsCalibrationDataPointStream(parser);
			List<CalibrationDataItem> fixings = calibrationDataset.getDataPoints().stream().filter(
					cdi -> cdi.getSpec().getProductName().equals(FIXING) || cdi.getSpec().getProductName().equals(DEPOSIT)).toList();

			Calibrator calibrator = new Calibrator(fixings, new CalibrationContextImpl(marketDataTime, 1E-9));
			final Optional<CalibrationResult> optionalCalibrationResult = calibrator.calibrateModel(calibrationItems, new CalibrationContextImpl(marketDataTime, 1E-9));

			AnalyticModel calibratedModel = optionalCalibrationResult.orElseThrow().getCalibratedModel();
			return calibratedModel;
		} catch (final Exception e) {
			throw new SDCException(ExceptionId.SDC_CALIBRATION_ERROR, e.getMessage());
		}
	}

	// Search for the nearest ESTR fixing and add it to the calibration items as 1-day discount rate proxy
	private void addOvernightDepositRate(CalibrationDataset calibrationDataset) {
		List<CalibrationDataItem> estrFixings = calibrationDataset.getFixingDataItems().stream().filter(fixingItem ->
				fixingItem.getSpec().getCurveName().equals("ESTR") &&
						fixingItem.getSpec().getMaturity().equals("1D")).toList();
		if (!estrFixings.isEmpty()) {
			CalibrationDataItem nearestFixing = estrFixings.stream()
					.min((item1, item2) -> {
						double diff1 = FloatingpointDate.getFloatingPointDateFromDate(calibrationDataset.getDate(), item1.getDateTime());
						double diff2 = FloatingpointDate.getFloatingPointDateFromDate(calibrationDataset.getDate(), item2.getDateTime());
						return Double.compare(Math.abs(diff1), Math.abs(diff2));
					})
					.orElse(null);
			if (nearestFixing != null) {
				CalibrationDataItem.Spec calibrationItemSpecON = new CalibrationDataItem.Spec("EUREST1D", "ESTR","Overnight-Rate","1D");
				CalibrationDataItem calibrationItemON = new CalibrationDataItem(calibrationItemSpecON, nearestFixing.getQuote(), nearestFixing.getDateTime());
				calibrationDataset.getCalibrationDataItems().add(calibrationItemON);
			}
		}
	}

	private InterestRateSwapLegProductDescriptor getInterestRateSwapLegProductDescriptor(String productData, LegType legType)  {
		SmartDerivativeContractDescriptor productDescriptor = getSmartderivativeContractDescriptor(productData);
		String ownerPartyID = productDescriptor.getUnderlyingReceiverPartyID();
		InterestRateSwapProductDescriptor underlying = (InterestRateSwapProductDescriptor) new FPMLParser(ownerPartyID, Calibrator.FORWARD_EUR_6M, Calibrator.DISCOUNT_EUR_OIS).getProductDescriptor(productDescriptor.getUnderlying());
		switch (legType) {
			case LEG_RECEIVER:
				return (InterestRateSwapLegProductDescriptor) underlying.getLegReceiver();
			case LEG_PAYER:
				return (InterestRateSwapLegProductDescriptor) underlying.getLegPayer();
			default:
				throw new IllegalArgumentException("Unknown leg type: " + legType.getValue());
		}
	}

}
