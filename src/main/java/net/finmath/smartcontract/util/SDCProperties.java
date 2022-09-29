/*
 * Initializing the properties from the configuration file.
 *
 * @author Dietmar Schnabel
 */
package net.finmath.smartcontract.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * The Class SDCProperties.
 * <p>
 * Initializes the properties from the sdc.properties file at program start.
 */
public class SDCProperties {

	private static final Logger logger = LoggerFactory.getLogger(SDCProperties.class);

	private static Properties properties;


	public static String getProperty(String p) {
		if (SDCUtil.isEmpty(p)) {
			logger.error("The property " + p + " is not defined!!");
		}
		return properties.getProperty(p);

	}

	public static boolean init(String connectionPropertiesFile) {
		try {
			properties = new Properties();
			properties.load(new FileInputStream(connectionPropertiesFile));
		} catch (Exception e) {
			System.out.println("SDCProperties: Not able to retrieve properties");
			System.exit(1);
		}
		return true;
	}

	/**
	 * Sets the defaults.
	 *
	 * @param prop properties
	 */
	private static void setDefaults(Properties prop) {

		prop.setProperty(SDCConstants.DATA_PATH, SDCConstants.DATA_PATH_DEFAULT);
		prop.setProperty(SDCConstants.MARKET_DATA_FILE_HEADER, SDCConstants.MARKET_DATA_FILE_HEADER_DEFAULT);
		prop.setProperty(SDCConstants.EVENT_HANDLER_PATH, SDCConstants.EVENT_HANDLER_PATH_DEFAULT);
		prop.setProperty(SDCConstants.URL_TIMEOUT, SDCConstants.URL_TIMEOUT_DEFAULT);

	}

	/**
	 * The URL_TIMEOUT property
	 *
	 * @return The URL_TIMEOUT property as int
	 */
	public static int getURL_TIMEOUT() {
		return Integer.parseInt(SDCProperties.getProperty(SDCConstants.URL_TIMEOUT));
	}


}
