package net.finmath.smartcontract.simulation;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import net.finmath.time.businessdaycalendar.BusinessdayCalendar;

public class MarketDataLoader {

    private final Path csvPath;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

    public MarketDataLoader(Path csvPath) {
        this.csvPath = csvPath;
    }

    // Skips all entries that are not business dates
    public List<MarketDataSnapshot> load(BusinessdayCalendar businessdayCalendar) throws IOException {
        List<MarketDataSnapshot> snapshots = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalStateException("Empty CSV: " + csvPath);
            }

            String[] headers = headerLine.split(";");
            if (headers.length < 2 || !"Date".equals(headers[0])) {
                throw new IllegalStateException("First column must be 'Date' in " + csvPath);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] fields = line.split(";");
                if (fields.length != headers.length) {
                    throw new IllegalStateException("Field count mismatch in line: " + line);
                }

                LocalDate date = LocalDate.parse(fields[0], dateFormatter);
                if(!businessdayCalendar.isBusinessday(date)) continue;
                
                double[] quotes = new double[fields.length - 1];
                for (int i = 1; i < fields.length; i++) {
                    quotes[i-1] = Double.parseDouble(fields[i]) / 100.0;
                }

                snapshots.add(new MarketDataSnapshot(date, quotes));
            }
            Collections.reverse(snapshots);
        }

        return snapshots;
    }
}

