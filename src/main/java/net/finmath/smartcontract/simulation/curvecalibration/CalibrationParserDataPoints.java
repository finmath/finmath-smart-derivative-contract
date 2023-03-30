package net.finmath.smartcontract.simulation.curvecalibration;

import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Parses calibration data points and converts it to calibration specs
 */
public class CalibrationParserDataPoints implements CalibrationParser {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CalibrationParserDataPoints.class);
	private final Set<String> maturityGrid = new HashSet<>(Arrays.asList("1W", "2W", "3W", "1M", "2M", "3M", "4M", "5M", "6M", "7M", "8M", "9M", "10M", "11M", "12M", "15M", "18M", "2Y", "3Y", "4Y", "5Y", "6Y", "7Y", "8Y", "9Y", "10Y", "15Y", "20Y", "25Y", "30Y", "40Y", "50Y"));

	@Override
	public Stream<CalibrationSpecProvider> parse(final Stream<CalibrationDatapoint> datapoints) {
		return datapoints.map(this::parseDatapointIfPresent).filter(Optional::isPresent).map(Optional::get);
	}

	private Optional<CalibrationSpecProvider> parseDatapointIfPresent(final CalibrationDatapoint datapoint) {
		if (!maturityGrid.contains(datapoint.getMaturity())) {
			return Optional.empty();
		}
		switch (datapoint.getCurveName()) {
			case "ESTR":
			case "EONIA":
				if (datapoint.getProductName().equals("Swap-Rate"))
					return Optional.of(new CalibrationSpecProviderOis(datapoint.getMaturity(), "annual", datapoint.getQuote() ));
				else
					return Optional.empty();
			case "Euribor6M":
				if (datapoint.getProductName().equalsIgnoreCase("Swap-Rate"))
					return Optional.of(new CalibrationSpecProviderSwap("6M", "semiannual", datapoint.getMaturity(), datapoint.getQuote() ));
				if (datapoint.getProductName().equalsIgnoreCase("Forward-Rate-Agreement"))
					return Optional.of(new CalibrationSpecProviderFRA("6M", datapoint.getMaturity(), datapoint.getQuote() ));
				if (datapoint.getProductName().equalsIgnoreCase("Cash"))
					return Optional.of(new CalibrationSpecProviderDeposit("6M", datapoint.getMaturity(), datapoint.getQuote() ));
				else
					return Optional.empty();
			case "Euribor1M":
				return Optional.of(new CalibrationSpecProviderSwap("1M", "monthly", datapoint.getMaturity(), datapoint.getQuote() ));
			case "Euribor3M":
				return Optional.of(new CalibrationSpecProviderSwap("3M", "quarterly", datapoint.getMaturity(), datapoint.getQuote() ));
			default:
				logger.warn("Ignored data point.");
				return Optional.empty();
		}
	}
}
