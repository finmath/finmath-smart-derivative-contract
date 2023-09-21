package net.finmath.smartcontract.marketdata.curvecalibration;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * IR Market Data Scenario Class holds a SecnarioDate an a Map containing CurveData
 *
 * @author Peter Kohl-Landgraf
 */
public class CalibrationDataset {

	LocalDateTime scenarioDate;
	Set<CalibrationDataItem> calibrationDataItems;
	Set<CalibrationDataItem> fixingDataItems;

	public CalibrationDataset(final Set<CalibrationDataItem> curveDataPointSet, final LocalDateTime scenarioDate) {
		this.scenarioDate = scenarioDate;
		this.fixingDataItems  = curveDataPointSet.stream().filter(dataItem->dataItem.getProductName().equals("Fixing")).sorted(Comparator.comparing(CalibrationDataItem::getDate)).collect(Collectors.toCollection( LinkedHashSet::new ));
		this.calibrationDataItems = curveDataPointSet.stream().filter(dataItem->!dataItem.getProductName().equals("Fixing")).sorted(Comparator.comparing(CalibrationDataItem::getDaysToMaturity)).collect(Collectors.toCollection( LinkedHashSet::new ));

	}

	public CalibrationDataset getScaled(double scaleFactor){

		Set<CalibrationDataItem> scaledSet = calibrationDataItems.stream().map(point->point.getClonedScaled(scaleFactor)).collect(Collectors.toCollection( LinkedHashSet::new ));
		return new CalibrationDataset(scaledSet, scenarioDate);
	}


	public Set<CalibrationDataItem> getFixingDataItems(){
		return this.fixingDataItems;
	}

	/*public Map<LocalDate,Double>  getFixingDataMap(){
		return fixingDataItems.stream().sorted(Comparator.comparing(CalibrationDataItem::getDate))
				.collect(Collectors.toMap(item->item.getMaturity(), item->item.getQuote(), (x, y) -> y, LinkedHashMap::new));
	}*/

	public CalibrationDataset getClonedFixingsAdded(Set<CalibrationDataItem> newFixingDataItems){
		// @todo - do several checks i.e. what if a fixing already exists at that date or if dataTiem is not a fixing
		Set<CalibrationDataItem> clone = new LinkedHashSet<>(); /*Deep Clone*/
		clone.addAll(this.calibrationDataItems);
		clone.addAll(this.fixingDataItems);
		newFixingDataItems.stream().forEach(newFixing-> {
			if (newFixing.getProductName().equals("Fixing") ) {
				if (!this.fixingDataItems.stream().filter(fixing->fixing.getCurveName().equals(newFixing.getCurveName()) && fixing.getDate().equals(newFixing.getDate())).findAny().isPresent() )
					clone.add(newFixing);
			}
		});
		return new CalibrationDataset(clone, this.scenarioDate);
	}


	public String  serializeToJson() {
		ObjectMapper mapper = new ObjectMapper();

		String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
		String quoteKey = "Quotes";
		String fixingKey = "Fixings";

		Map<String,Map<String, Map<String,Map<String, Map<String, Double > > > > > nestedMap = new LinkedHashMap<>();
		nestedMap.put(date,new LinkedHashMap<>());
		nestedMap.get(date).put(quoteKey, new LinkedHashMap<>());
		/* Serialize Quotes */
		for (CalibrationDataItem item : this.calibrationDataItems){
			if ( !nestedMap.get(date).get(quoteKey).containsKey(item.getSpec().getCurveName()) )
				nestedMap.get(date).get(quoteKey).put(item.getSpec().getCurveName(),new LinkedHashMap<>());
			if ( ! nestedMap.get(date).get(quoteKey).get(item.getSpec().getCurveName()).containsKey(item.getSpec().getProductName()))
				nestedMap.get(date).get(quoteKey).get(item.getSpec().getCurveName()).put(item.getSpec().getProductName(),new LinkedHashMap<>());
			nestedMap.get(date).get(quoteKey).get(item.getSpec().getCurveName()).get(item.getSpec().getProductName()).put(item.getSpec().getMaturity(),item.getQuote()); /*Maturity is mapped to Quote*/
		}
		/* Serialize Fixings */
		nestedMap.get(date).put(fixingKey, new LinkedHashMap<>());
		for(CalibrationDataItem item : this.fixingDataItems){
			if ( !nestedMap.get(date).get(fixingKey).containsKey(item.getSpec().getCurveName()) )
				nestedMap.get(date).get(fixingKey).put(item.getSpec().getCurveName(),new LinkedHashMap<>());
			if ( !nestedMap.get(date).get(fixingKey).get(item.getSpec().getCurveName()).containsKey(item.getSpec().getProductName()))
				nestedMap.get(date).get(fixingKey).get(item.getSpec().getCurveName()).put(item.getSpec().getProductName(),new LinkedHashMap<>());
			nestedMap.get(date).get(fixingKey).get(item.getSpec().getCurveName()).get(item.getSpec().getProductName()).put(item.getDateString(),item.getQuote()); /*Date is mapped to Fixing*/
		}
		try {
			String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(nestedMap);
			return json;
		}
		catch (Exception e){
			return null;
		}

	}



	/**
	 * Returns a Stream of CalibrationSpecs, curveData provided as calibration data points, will be converted to calibration specs
	 * Currently Swap-Rates, FRAS and Deposit Specs are used.
	 *
	 * @param parser Object implementing a CalibrationParser.
	 * @return Stream of calibration spec providers.
	 */
	public Stream<CalibrationSpecProvider> getDataAsCalibrationDataPointStream(final CalibrationParser parser) {
		/* Return only calibraiton specs EXCEPT Past Fixings and spot data */
		return parser.parse(calibrationDataItems.stream().filter(dataItem-> !dataItem.getProductName().equals("Fixing") && !dataItem.getProductName().equals("Deposit")));

	}

	public Set<CalibrationDataItem> getDataPoints(){
		return this.calibrationDataItems;
	}

	public LocalDateTime getDate() {
		return scenarioDate;
	}
}
