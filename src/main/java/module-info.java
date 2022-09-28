module net.finmath.smartderivativecontract {
	exports net.finmath.smartcontract.valuation;
	exports net.finmath.smartcontract.valuation.scenariogeneration;
	exports net.finmath.smartcontract.client;
	exports net.finmath.smartcontract.util;
	exports net.finmath.smartcontract.demo;
	exports net.finmath.smartcontract.product.xml;
	exports net.finmath.smartcontract.oracle;
	exports net.finmath.smartcontract.oracle.historical;
	exports net.finmath.smartcontract.simulation.scenariogeneration;
	exports net.finmath.smartcontract.simulation.curvecalibration;

	opens net.finmath.smartcontract.service to java.base;
	requires io.swagger.v3.oas.annotations;

	requires java.datatransfer;
	requires java.logging;
	requires java.validation;
	requires java.annotation;
	requires java.desktop;

	requires spring.core;
	requires spring.web;
	requires spring.context;
	requires spring.security.core;
	requires spring.security.web;
	requires spring.security.config;
	requires spring.statemachine.core;

	requires org.apache.commons.lang3;
	requires jackson.databind.nullable;
	requires org.apache.tomcat.embed.core;
	requires com.google.gson;
	requires org.slf4j;
	requires net.finmath.lib;
	requires money.api;
	requires moneta;
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.dataformat.csv;
	requires com.fasterxml.jackson.databind;
	requires javafx.swing;
	requires spring.boot.autoconfigure;
	requires spring.boot;
	requires com.google.common;
	requires org.jfree.jfreechart;
	requires net.finmath.plots;
}