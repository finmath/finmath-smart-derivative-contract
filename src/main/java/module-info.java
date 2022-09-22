module net.finmath.smartderivativecontract {
	exports net.finmath.smartcontract.valuation;
	exports net.finmath.smartcontract.valuation.scenariogeneration;
	exports net.finmath.smartcontract.client;
	exports net.finmath.smartcontract.util;
	exports net.finmath.smartcontract.demo;
	exports net.finmath.smartcontract.xml;
	exports net.finmath.smartcontract.oracle.historical;
	exports net.finmath.smartcontract.simulation.scenariogeneration;
	exports net.finmath.smartcontract.simulation.curvecalibration;

	requires com.google.gson;
	requires slf4j.api;
	requires spring.core;
	requires spring.web;
	requires net.finmath.lib;
	requires jfreechart;
	requires money.api;
	requires java.desktop;
	requires moneta;
	requires finmath.lib.plot.extensions;
	requires com.fasterxml.jackson.dataformat.csv;
	requires com.fasterxml.jackson.databind;
	requires spring.statemachine.core;
	requires javafx.swing;
	requires jcommon;
	requires spring.boot.autoconfigure;
	requires spring.boot;
	requires com.google.common;
}