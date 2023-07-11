package net.finmath.smartcontract.marketdata.database;

import jakarta.annotation.PostConstruct;
import jnr.ffi.annotations.Out;
import net.finmath.smartcontract.service.utils.ResourceGovernor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static java.util.stream.Collectors.joining;

/**
 * Class that provides functionalities necessary to get/write data to the market data database.
 *
 * @author Luca Bressan
 */
@Service
@Profile("!test")
public class DatabaseConnector {


    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnector.class);
    private Connection connection;
    @Autowired
    private ResourceGovernor resourceGovernor;


    /**
     * Autowiring-compliant constructor. Credentials file location and temporary file location must be specified in application.yml
     */
    @PostConstruct
    public void init() {
        try {
            Properties databaseConnectionProperties = new Properties();
            databaseConnectionProperties.load(new StringReader(
                    resourceGovernor.getDatabasePropertiesAsResourceInReadMode()
                                    .getContentAsString(StandardCharsets.UTF_8)));
            connection = DriverManager.getConnection(databaseConnectionProperties.getProperty("URL"),
                                                     databaseConnectionProperties.getProperty("USERNAME"),
                                                     databaseConnectionProperties.getProperty("PASSWORD"));
            logger.info("Connected to the PostgreSQL server successfully.");
        } catch (SQLException | IOException e) {
            throw new IllegalStateException("Failed to autowire the database connector.", e);
        }

    }

    /**
     * Retrieves the latest market quotes and the full fixings history from the database and then writes to the active dataset file.
     *
     * @param fixingSymbols   Symbols which must be treated as fixings and not as quotes
     * @param username the name of the user that has performed the request
     * @throws SQLException when JDBC reports a failure in the DB transaction
     * @throws IOException  when writing to the active dataset fails
     */
    public void fetchFromDatabase(List<String> fixingSymbols, String username) throws SQLException, IOException {
        Statement fetchStatement = connection.createStatement();
        String rawOutput;
        /*
        Explaination of the query:
        SELECT the most recent quote/fixing for all symbols provided
        AND APPEND the full history of those symbols which are marked as fixings
        THEN order the results in chronological order
        THEN format the output as a JSON market data transfer message
         */
        if (fetchStatement.execute("""
                                           SELECT json_build_object('requestTimestamp',to_json((now() at time zone 'utc')::timestamptz(0)),'values',json_agg(t1)) FROM (
                                           SELECT 	"dataTimestamp_"::timestamptz(0) as "dataTimestamp",
                                           "symbolId_" as "symbol",
                                           "value_" as "value"
                                           FROM (SELECT * FROM (SELECT T10."symbolId_", T10."dataTimestamp_", T10."value_" FROM
                                           public."MarketDataPoints" AS T10, public."MarketDataPoints" AS T20
                                           WHERE T10."symbolId_" = T20."symbolId_"
                                           GROUP BY  T20."symbolId_",  T10."dataTimestamp_", T10."symbolId_"
                                           HAVING T10."dataTimestamp_" = MAX(t20."dataTimestamp_")
                                           ORDER BY T10."symbolId_") AS T30
                                           UNION SELECT "symbolId_", "dataTimestamp_", "value_" FROM public."MarketDataPoints"
                                           WHERE "symbolId_" IN (%s)
                                           ORDER BY "symbolId_") AS T5) AS T1;
                                           """.formatted(fixingSymbols.stream().collect(joining("','", "'", "'")))
                                              .replaceFirst(".$",
                                                            ""))) { // PostgreSQL JSON parser won't parse multiline-JSONs correctly, remove all newline chars
            if (fetchStatement.getResultSet().next()) {
                rawOutput = fetchStatement.getResultSet().getString(1);
                logger.info("Writing dataset...");
                logger.info(rawOutput);
                try(OutputStream outputStream = resourceGovernor.getActiveDatasetAsResourceInWriteMode(username).getOutputStream()) {
                    outputStream.write(rawOutput.getBytes());
                    logger.info("...done.");
                }
            }
        }
        fetchStatement.close();
    }

    /**
     * Scans the import candidates file and sends a table update request with the new market data.
     *
     * @throws SQLException when JDBC reports a failure in the DB transaction
     * @throws IOException if opening the import candidates file was not possible
     */
    public void updateDatabase() throws SQLException, IOException {
        Statement importTableCreationStatement = connection.createStatement();
        Statement importStatement = connection.createStatement();
        Statement clearAfterUpdateStatement = connection.createStatement();
        String importfileLocation = resourceGovernor.getImportCandidateAsResourceInReadMode().getFile().getAbsolutePath();


        /*
        Explaination of the query:
        IF NOT EXISTS CREATE a temporary table containing a single record´ready to contain the import candidates text
        CLEAR the table
        COPY the import candidates to the table
         */
        String sql = """
                CREATE UNLOGGED TABLE IF NOT EXISTS import_helper (import_helper text);
                DELETE FROM import_helper;
                COPY import_helper FROM '%s';
                """.formatted(importfileLocation);
        importTableCreationStatement.execute(sql);
        /*
        Explaination of the query:
        parse the import candidate from the temporary table
        if there are conflict with already imported quotes, do nothing and keep the old version
         */
        importStatement.execute("""
                                        INSERT INTO public."MarketDataPoints"
                                          SELECT 	(dataPoints_ ->> 'dataTimestamp')::timestamp without time zone AS dataTimestamp_,
                                        	(dataPoints_ ->> 'symbol')::varchar AS symbolId_,
                                        	((dataPoints_ ->> 'value')::double precision)/100 AS value_,
                                        	1 AS owner_,
                                        	'remote' AS source_ FROM (
                                        SELECT json_array_elements(values_) AS dataPoints_ FROM (
                                        	SELECT string_agg(import_helper, '')::json -> 'values' AS values_ FROM import_helper
                                        	) AS t1
                                        ) AS t2
                                          ON CONFLICT ON CONSTRAINT "MarketDataPoints_pkey" DO NOTHING;
                                        """);
                /*
        Explaination of the query:
        remove the temporary table
         */
        clearAfterUpdateStatement.execute("DROP TABLE IF EXISTS import_helper;");
        importTableCreationStatement.close();
        importStatement.close();
        clearAfterUpdateStatement.close();
    }
}
