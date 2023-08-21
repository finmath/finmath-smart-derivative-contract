package net.finmath.smartcontract.marketdata.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Service
@Profile("test")
public class MockDatabaseConnector extends DatabaseConnector {

    private static final Logger logger = LoggerFactory.getLogger(MockDatabaseConnector.class);

    @Override
    public void init() {
        logger.info("Initialized mock connection.");
    }

    @Override
    public void fetchFromDatabase(List<String> fixingSymbols, String username) throws SQLException, IOException {
        throw new UnsupportedOperationException("This operation is disabled during testing.");
    }

    @Override
    public void updateDatabase() throws SQLException, IOException {
        throw new UnsupportedOperationException("This operation is disabled during testing.");
    }
}
