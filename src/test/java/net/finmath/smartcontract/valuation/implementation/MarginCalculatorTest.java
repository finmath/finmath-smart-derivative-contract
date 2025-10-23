package net.finmath.smartcontract.valuation.implementation;

import net.finmath.smartcontract.model.MarginResult;
import net.finmath.smartcontract.model.ValueResult;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import net.finmath.smartcontract.valuation.client.ValuationClient;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataItem;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationDataset;
import net.finmath.smartcontract.valuation.marketdata.curvecalibration.CalibrationParserDataItems;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

class MarginCalculatorTest {

	@Test
	void testMargin() throws Exception {
		final String marketDataStart = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/md_testset1.xml").readAllBytes(), StandardCharsets.UTF_8);
		final String marketDataEnd = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/md_testset2.xml").readAllBytes(), StandardCharsets.UTF_8);
		final String product = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract.xml").readAllBytes(), StandardCharsets.UTF_8);

		MarginCalculator marginCalculator = new MarginCalculator();
		MarginResult valuationResult = marginCalculator.getValue(marketDataStart, marketDataEnd, product);

		double value = valuationResult.getValue().doubleValue();

		Assertions.assertEquals(14877.83, value, 0.005, "Margin");
		System.out.println(valuationResult);
	}

	// Excluding test due to missing files.
	//@Test
	void testValue() throws Exception {
		final String marketData = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/sdc_initial_trade_settlement_1747900094507.xml").readAllBytes(), StandardCharsets.UTF_8);
		final String product = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/sdc_product_1747899941750.xml").readAllBytes(), StandardCharsets.UTF_8);

		MarginCalculator marginCalculator = new MarginCalculator();
		ValueResult valuationResult = marginCalculator.getValue(marketData, product);
		LocalDateTime refDate = LocalDateTime.of(2025,5,22,0,0);
		ValueResult valueResult = marginCalculator.getValueAtEvaluationTime(marketData,product, refDate);
		ValueResult valueResultPev = marginCalculator.getValueAtEvaluationTime(marketData,product, refDate.minusDays(3));


		double valuePrev = valueResultPev.getValue().doubleValue();
		double value = valueResult.getValue().doubleValue();

		double diff = value-valuePrev;

		Assertions.assertEquals(926403.97, value, 0.005, "Valuation");
		System.out.println(valuationResult);
	}

	@Test
	void testAccrual() throws Exception{
		final String marketData = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net/finmath/smartcontract/valuation/client/md_testset_with_fixings.xml").readAllBytes(), StandardCharsets.UTF_8);
		final String product = new String(ValuationClient.class.getClassLoader().getResourceAsStream("net.finmath.smartcontract.product.xml/smartderivativecontract.xml").readAllBytes(), StandardCharsets.UTF_8);

		SmartDerivativeContractDescriptor productDescriptor = null;
		try {
			productDescriptor = SDCXMLParser.parse(product);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		}

		CalibrationDataset set = CalibrationParserDataItems.getCalibrationDataSetFromXML(marketData, productDescriptor.getMarketdataItemList());
		CalibrationDataItem item = set.getFixingDataItems().stream()
				.filter(f->f.getSpec().getKey().equals("ESTRFIX1D")).findAny().get();
		double esterFixing = item.getQuote();

		MarginCalculator marginCalculator = new MarginCalculator();
		ValueResult valuationResult = marginCalculator.getValue(marketData, product);
		double valuePrev = valuationResult.getValue().doubleValue(); // value as of scenarioDate 2023-01-31T14:35:23
		ValueResult valuationResultNext = marginCalculator.getValueAtEvaluationTime(marketData, product, set.getDate().plusDays(1));
		double valueNext = valuationResultNext.getValue().doubleValue();
		double valueAccrued = valuePrev * (1.0 + esterFixing / 360.0);
		Assertions.assertEquals(valueNext, valueAccrued,0.01, "Accrual: " + (valueNext - valueAccrued));

	}




}
