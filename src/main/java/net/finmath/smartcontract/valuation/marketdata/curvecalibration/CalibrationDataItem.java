package net.finmath.smartcontract.valuation.marketdata.curvecalibration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("java:S125")
public class CalibrationDataItem {
	private static final String REGEX = "((?<=[a-zA-Z])(?=[0-9]))|((?<=[0-9])(?=[a-zA-Z]))";


	public static class Spec {
		private final String key;
		private final String curveName;
		private final String productName;
		private final String maturity;

		public Spec(final String key, final String curveName, final String productName, final String maturity) {
			this.key = key;
			this.curveName = curveName;
			this.productName = productName;
			this.maturity = maturity;

		}


		public String getKey() {return key;}

		public String getCurveName() {
			return curveName;
		}

		public String getProductName() {
			return productName;
		}

		public String getMaturity() {
			return maturity;
		}


		@Override
		public int hashCode() {
			return Objects.hash(key, curveName, productName, maturity);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Spec spec = (Spec) o;
			return Objects.equals(key, spec.key) && Objects.equals(curveName, spec.curveName) && Objects.equals(productName, spec.productName) && Objects.equals(maturity, spec.maturity);
		}
	}

	final CalibrationDataItem.Spec spec;
	final Double quote;
	final LocalDateTime dateTime;

    /*public CalibrationDataItem(String curve, String productName, String maturity, Double quote){
        spec = new Spec("",curve,productName,maturity);
        this.quote = quote;
        this.dateTime=null;
    }*/

	public CalibrationDataItem(final CalibrationDataItem.Spec spec, Double quote, LocalDateTime dateTime) {
		this.spec = spec;
		this.quote = quote;
		this.dateTime = dateTime;
	}


	public CalibrationDataItem getClonedScaled(double factor) {
		return new CalibrationDataItem(spec, quote / factor, dateTime);
	}

	public CalibrationDataItem getClonedShifted(double amount) {
		return new CalibrationDataItem(spec, quote + amount, dateTime);
	}

	public CalibrationDataItem.Spec getSpec() {
		return spec;
	}

	public String getCurveName() {
		return getSpec().getCurveName();
	}

	public String getProductName() {
		return getSpec().getProductName();
	}

	public String getMaturity() {
		return getSpec().getMaturity();
	}


	public Double getQuote() {
		return quote;
	}


	public Integer getDaysToMaturity() {
		List<String> list = Arrays.asList(getSpec().getMaturity().split(REGEX));
		int nTimeUnits = Integer.parseInt(list.get(0));
		String timeUnitKey = list.get(1);
		if (timeUnitKey.equals("D"))
			return nTimeUnits;
		if (timeUnitKey.equals("M"))
			return nTimeUnits * 30;
		if (timeUnitKey.equals("Y"))
			return nTimeUnits * 360;
		else
			return 0;
	}

	public String getDateString() {
		return this.dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}


	public LocalDate getDate() {return dateTime.toLocalDate();}

	public LocalDateTime getDateTime(){ return dateTime;}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CalibrationDataItem that = (CalibrationDataItem) o;
		return Objects.equals(spec, that.spec) && Objects.equals(quote, that.quote) && Objects.equals(dateTime, that.dateTime);
	}

	@Override
	public int hashCode() {
		return Objects.hash(spec, quote, dateTime);
	}
}
