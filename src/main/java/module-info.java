module net.finmath.smartderivativecontract {
	exports net.finmath.smartcontract.api;
	exports net.finmath.smartcontract.model;
	exports net.finmath.smartcontract.valuation;
	exports net.finmath.smartcontract.client;
	exports net.finmath.smartcontract.demo;
	exports net.finmath.smartcontract.product;
	exports net.finmath.smartcontract.product.xml;
	exports net.finmath.smartcontract.service;
	exports net.finmath.smartcontract.service.config;
	exports net.finmath.smartcontract.oracle;
	exports net.finmath.smartcontract.oracle.interestrates;
	exports net.finmath.smartcontract.marketdata.curvecalibration;
	exports net.finmath.smartcontract.service.utils;
	exports net.finmath.smartcontract.marketdata.adapters;
	exports net.finmath.smartcontract.webflux;
    exports net.finmath.smartcontract.reactive;

	opens net.finmath.smartcontract.client;
	opens net.finmath.smartcontract.product.xml;
	opens net.finmath.smartcontract.service to java.base, spring.core;
	opens net.finmath.smartcontract.service.config to spring.core;
	opens net.finmath.smartcontract.webflux to spring.core;
	exports net.finmath.smartcontract.service.controllers;
	opens net.finmath.smartcontract.service.controllers to java.base, spring.core;

	// --- ALL CHANGED MODULES ARE BECAUSE OF OBSOLESCENCE OR REQUIRED BY THE NEW FEATURES ---
	// requires static org.apache.tomcat.embed.core; <-- Spring Boot 3.x.y is JakartaEE 10 compliant, this module is superseded by jakarta.servlet
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.dataformat.csv;
	requires com.fasterxml.jackson.dataformat.javaprop;
	requires com.fasterxml.jackson.datatype.jdk8;
	requires com.fasterxml.jackson.datatype.jsr310;
	requires io.swagger.v3.oas.annotations;
	requires jakarta.annotation; //<-- javax has been transferred to the JakartaEE project, the respective modules have been superseded
	requires jakarta.persistence;
	requires jakarta.servlet;
	requires jakarta.validation;
	requires jakarta.xml.bind;
	requires java.datatransfer;
	requires java.desktop;
	requires java.logging;
	requires java.money;
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.swing;
	requires net.finmath.lib;
	requires net.finmath.plots;
	requires nv.websocket.client;
	requires org.apache.commons.lang3;
	requires org.apache.httpcomponents.httpclient;
	requires org.apache.httpcomponents.httpcore;
	requires org.javamoney.moneta;
	requires org.jfree.jfreechart;
	requires org.openapitools.jackson.nullable;
	requires org.slf4j;
	requires reactor.core;
	requires spring.beans;
	requires spring.boot.autoconfigure;
	requires spring.boot;
	requires spring.context;
	requires spring.core;
	requires spring.messaging;
	requires spring.security.config;
	requires spring.security.core;
	requires spring.security.web;
	requires spring.statemachine.core;
	requires spring.web;
	requires spring.webflux;
	requires spring.webmvc; //<-- required for CORS config
	requires transitive io.reactivex.rxjava3;
}
