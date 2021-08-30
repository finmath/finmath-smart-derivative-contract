/**
 * Spring-boot application, starts the valuation REST service
 *
 * @author Dietmar Schnabel
 */

package net.finmath.smartcontract.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import net.finmath.smartcontract.util.SDCStarter;


@SpringBootApplication
public class Application {
	/**
	 * Application entry point.
	 *
	 * @param args Program arguments (not used).
	 */
	public static void main(String[] args) {
		SDCStarter.init(args);
		SpringApplication.run(Application.class, args);
	}
}
