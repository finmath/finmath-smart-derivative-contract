package net.finmath.smartcontract.simulation.curvecalibration;

import java.util.stream.Stream;

public interface CalibrationParser {
	Stream<CalibrationSpecProvider> parse(Stream<CalibrationDatapoint> datapoints);
}
