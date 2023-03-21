/*
 /*
 * (c) Copyright Christian P. Fries, Germany. Contact: email@christianfries.com.
 *
 * Created on 15 Oct 2018
 */

package net.finmath.smartcontract.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import net.finmath.information.Library;
import net.finmath.smartcontract.api.InfoApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

/**
 * Controller for the settlement valuation REST service.
 * TODO Refactor try/catch once openapi can generate exception handling
 *
 * @author Christian Fries
 * @author Peter Kohl-Landgraf
 */
@RestController
public class InfoController implements InfoApi {

	private final Logger logger = LoggerFactory.getLogger(InfoController.class);

	/**
	 *
	 * @return String Json representing the info on Git
	 */
	@Override
	public ResponseEntity<String> infoGit() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Responded", "info git");

		try(InputStream propertiesInputStream = InfoController.class.getResourceAsStream("/git.properties")) {
			JavaPropsMapper mapper = new JavaPropsMapper();
			ObjectNode node = mapper.readValue(propertiesInputStream, ObjectNode.class);
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

			String info = ow.writeValueAsString(node);
			return ResponseEntity.ok(info);
		} catch (final Exception e) {
			logger.error("Failed to get git info.");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 *
	 * @return String Json representing the info on the Library
	 */
	@Override
	public ResponseEntity<String> infoFinmath() {

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Responded", "info finmath");

		try(InputStream propertiesInputStream = Library.class.getResourceAsStream("/finmath-lib.properties")) {
			JavaPropsMapper mapper = new JavaPropsMapper();
			ObjectNode node = mapper.readValue(propertiesInputStream, ObjectNode.class);
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

			String info = ow.writeValueAsString(node);
			return ResponseEntity.ok(info);
		} catch (final Exception e) {
			logger.error("Failed to get git info.");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
