/*
 * 
 *
 * @author Dietmar Schnabel
 */
package net.finmath.smartcontract.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;


/**
 * The Class SDCStarter.
 * General startup class for all applications.
 */
public class SDCStarter {
	
	private static final Logger logger = LoggerFactory.getLogger(SDCStarter.class);

	/**
	 * Inits the.
	 *
	 * @param args the property file name
	 */
	public static void init(String[] args) {

		if(args.length==0) {
			logger.error("You must start the application with the <property file> parameter! Args: {}", Arrays.toString(args));
			System.exit(1);
		}
		logger.info("Running with args {}", Arrays.toString(args));

		if(SDCUtil.isEmpty(System.getenv(SDCConstants.SDC_HOME))) {
			logger.error("You must define the " + SDCConstants.SDC_HOME + " environment variable!!");
			System.exit(1);
		}
		logger.info("SDC_HOME = " + System.getenv(SDCConstants.SDC_HOME));
		
		String propFile = System.getenv(SDCConstants.SDC_HOME) + File.separator + "etc" + File.separator + args[0] + ".properties";
		logger.info("Searching for properties file : " + propFile);
		if(!Files.exists(new File(propFile).toPath())) {
			logger.error("Property file: " + propFile + " does not exist!!");
			System.exit(1);
		}
		logger.info("Found Property file: " + propFile);
		SDCProperties.init(propFile);
	}

}
