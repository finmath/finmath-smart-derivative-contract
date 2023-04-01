
export class TradeDescriptor{
  firstCounterparty: string;
  secondCounterparty: string;
  marginBufferAmount: number;
  terminationFeeAmount:number;
  notionalAmount: number;
  currency: string;
  tradeDate: Date;
  effectiveDate: Date;
  terminationDate: Date;
  fixedPayingParty: string;
  fixedRate: number;
  fixedDayCountFraction: string;
  floatingPayingParty: string;
  floatingRateIndex: string;
  floatingDayCountFraction: string;
  floatingFixingDayOffset: string;
  floatingPaymentFrequency: string;

  constructor(firstCounterparty: string, secondCounterparty: string, marginBufferAmount: number, terminationFeeAmount: number, notionalAmount: number, currency: string, tradeDate: Date, effectiveDate: Date, terminationDate: Date, fixedPayingParty: string, fixedRate: number, fixedDayCountFraction: string, floatingPayingParty: string, floatingRateIndex: string, floatingDayCountFraction: string, floatingFixingDayOffset: string, floatingPaymentFrequency: string) {
    this.firstCounterparty = firstCounterparty;
    this.secondCounterparty = secondCounterparty;
    this.marginBufferAmount = marginBufferAmount;
    this.terminationFeeAmount = terminationFeeAmount;
    this.notionalAmount = notionalAmount;
    this.currency = currency;
    this.tradeDate = tradeDate;
    this.effectiveDate = effectiveDate;
    this.terminationDate = terminationDate;
    this.fixedPayingParty = fixedPayingParty;
    this.fixedRate = fixedRate;
    this.fixedDayCountFraction = fixedDayCountFraction;
    this.floatingPayingParty = floatingPayingParty;
    this.floatingRateIndex = floatingRateIndex;
    this.floatingDayCountFraction = floatingDayCountFraction;
    this.floatingFixingDayOffset = floatingFixingDayOffset;
    this.floatingPaymentFrequency = floatingPaymentFrequency;
  }
}
