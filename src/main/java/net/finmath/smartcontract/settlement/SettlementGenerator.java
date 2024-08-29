package net.finmath.smartcontract.settlement;

import net.finmath.smartcontract.model.ExceptionId;
import net.finmath.smartcontract.model.MarketDataList;
import net.finmath.smartcontract.model.SDCException;
import net.finmath.smartcontract.product.SmartDerivativeContractDescriptor;
import net.finmath.smartcontract.product.xml.SDCXMLParser;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SettlementGenerator {

	private Settlement settlement;

	public SettlementGenerator generateInitialSettlementXml(String marketDataXml, SmartDerivativeContractDescriptor sdc){
		generateSettlement(marketDataXml, Settlement.SettlementType.INITIAL, sdc, BigDecimal.ZERO);
		return this.settlementValuePrevious(BigDecimal.ZERO);
	}

	public SettlementGenerator generateRegularSettlementXml(String marketDataXml, SmartDerivativeContractDescriptor sdc, BigDecimal marginValue){
		return generateSettlement(marketDataXml, Settlement.SettlementType.REGULAR, sdc, marginValue);
	}

	private SettlementGenerator generateSettlement(String marketDataXml, Settlement.SettlementType settlementType, SmartDerivativeContractDescriptor sdc, BigDecimal marginValue){
		MarketDataList marketDataList = SDCXMLParser.unmarshalXml(marketDataXml, MarketDataList.class);
		settlement = new Settlement();
		settlement.setTradeId(sdc.getDltTradeId());
		settlement.setSettlementType(settlementType);
		settlement.setCurrency(sdc.getCurrency());
		settlement.setMarginValue(marginValue);
		settlement.setSettlementTime(ZonedDateTime.now());
		settlement.setMarketData(marketDataList);

		return this;
	}

	public SettlementGenerator marginLimits(List<BigDecimal> marginLimits){
		settlement.setMarginLimits(marginLimits);
		return this;
	}

	public SettlementGenerator settlementValue(BigDecimal settlementValue){
		settlement.setSettlementValue(settlementValue);
		return this;
	}

	public SettlementGenerator settlementValuePrevious(BigDecimal settlementValuePrevious){
		settlement.setSettlementValuePrevious(settlementValuePrevious);
		return this;
	}

	public SettlementGenerator settlementTimeNext(ZonedDateTime settlementTimeNext){
		settlement.setSettlementTimeNext(settlementTimeNext);
		return this;
	}

	public SettlementGenerator settlementValueNext(BigDecimal settlementValueNext){
		settlement.setSettlementValueNext(settlementValueNext);
		return this;
	}

	public String build(){
		if(allFieldsSet(settlement))
			return SDCXMLParser.marshalClassToXMLString(settlement);
		else
			throw new SDCException(ExceptionId.SDC_WRONG_INPUT, "settlement input incomplete", 400);
	}

	private static boolean allFieldsSet(Settlement settlement){
		return Arrays.stream(settlement.getClass().getDeclaredFields())
				.peek(f -> f.setAccessible(true))
				.map(f -> getFieldValue(f, settlement))
				.allMatch(Objects::nonNull);
	}

	private static Object getFieldValue(Field field, Object target) {
		try {
			return field.get(target);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
