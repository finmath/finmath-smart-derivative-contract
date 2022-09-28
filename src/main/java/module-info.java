module net.finmath.smartderivativecontract {
	exports net.finmath.smartcontract.api;
	exports net.finmath.smartcontract.valuation;
	exports net.finmath.smartcontract.client;
	exports net.finmath.smartcontract.util;
	exports net.finmath.smartcontract.demo;
	exports net.finmath.smartcontract.product;
	exports net.finmath.smartcontract.product.xml;
	exports net.finmath.smartcontract.service;
	exports net.finmath.smartcontract.service.config;
	exports net.finmath.smartcontract.oracle;
	exports net.finmath.smartcontract.oracle.historical;
	exports net.finmath.smartcontract.simulation.scenariogeneration;
	exports net.finmath.smartcontract.simulation.curvecalibration;
	exports net.finmath.smartcontract.service.utils;

	opens net.finmath.smartcontract.service to java.base, spring.core;
	opens net.finmath.smartcontract.service.config to spring.core;

	requires io.swagger.v3.oas.annotations;

	requires java.datatransfer;
	requires java.logging;
	requires java.validation;
	requires java.annotation;
	requires java.desktop;

	requires javafx.swing;

	requires spring.core;
	requires spring.web;
	requires spring.boot;
	requires spring.boot.autoconfigure;
	requires spring.context;
	requires spring.security.core;
	requires spring.security.web;
	requires spring.security.config;
	requires spring.statemachine.core;
	requires spring.beans;

	requires org.apache.commons.lang3;
	requires org.apache.tomcat.embed.core;
	requires jackson.databind.nullable;
	requires com.google.gson;
	requires org.slf4j;
	requires money.api;
	requires moneta;
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.dataformat.csv;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.datatype.jdk8;
	requires com.fasterxml.jackson.datatype.jsr310;
	requires com.google.common;

	requires net.finmath.lib;
	requires net.finmath.plots;

	requires org.jfree.jfreechart;
}