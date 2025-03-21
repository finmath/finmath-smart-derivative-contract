package net.finmath.smartcontract.valuation.marketdata.curvecalibration;

import java.util.stream.Stream;

/**
 * Interface for parsers generating <code>CalibrationSpecProvider</code> from <code>CalibrationDatapoint</code>.
 *
 * @author Luca Del Re
 * @author Peter Kohl-Landgraf
 * @author Christian Fries
 */
public interface CalibrationParser {
	Stream<CalibrationSpecProvider> parse(Stream<CalibrationDataItem> datapoints);
}
