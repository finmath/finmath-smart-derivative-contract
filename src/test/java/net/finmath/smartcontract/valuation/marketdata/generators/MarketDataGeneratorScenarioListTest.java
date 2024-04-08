package net.finmath.smartcontract.valuation.marketdata.generators;

import net.finmath.smartcontract.model.MarketDataList;
import net.finmath.smartcontract.model.SDCException;
import net.finmath.smartcontract.product.xml.SDCXMLParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class MarketDataGeneratorScenarioListTest {

	static MarketDataGeneratorScenarioList marketDataService;
	@BeforeAll
	static void before(){
		marketDataService = new MarketDataGeneratorScenarioList();
	}

	@Test
	void asObservable() {

		// before all
		assertEquals(0, marketDataService.getCounter());

		//given
		String filePath = "net/finmath/smartcontract/valuation/historicalMarketData/";
		AtomicReference<MarketDataList> observedMarketDataList = new AtomicReference<>();
		MarketDataList fileMarketDataList = new MarketDataList();

		//iteration 1
		String fileName1 = filePath + "marketdata_2008-05-02.xml";
		fileMarketDataList = SDCXMLParser.unmarshalXml(marketDataService.getMarketDataString(fileName1), MarketDataList.class);

		marketDataService.asObservable().subscribe(observedMarketDataList::set,                                           //onNext
			throwable -> System.out.println(throwable.getMessage()),    //onError
			() -> System.out.println("on complete"));

		assertEquals(1, marketDataService.getCounter());
		assertNotNull(fileMarketDataList);
		assertNotNull(observedMarketDataList);

		assertTrue(fileMarketDataList.equals(observedMarketDataList.get()));


		//iteration 2
		String fileName2 = filePath + "marketdata_2008-05-05.xml";
		fileMarketDataList = SDCXMLParser.unmarshalXml(marketDataService.getMarketDataString(fileName2), MarketDataList.class);

		marketDataService.asObservable().subscribe(observedMarketDataList::set,                                           //onNext
			throwable -> System.out.println(throwable.getMessage()),    //onError
			() -> System.out.println("on complete"));

		assertEquals(2, marketDataService.getCounter());
		assertNotNull(fileMarketDataList);
		assertNotNull(observedMarketDataList);

		assertTrue(fileMarketDataList.equals(observedMarketDataList.get()));
	}

	@Test
	void getMarketDataString_WrongFileNameSDCException(){
		assertThrows(SDCException.class, () -> marketDataService.getMarketDataString("wrongFileName"));
	}
}