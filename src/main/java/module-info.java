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

	requires java.datatransfer;
	requires java.logging;
	requires jakarta.validation;
	requires jakarta.annotation;
	requires java.desktop;

	requires javafx.swing;
	requires javafx.graphics;
	requires javafx.controls;

	requires spring.core;
	requires spring.web;
	requires spring.boot;
	requires spring.boot.autoconfigure;
	requires spring.context;
	requires spring.messaging;
	requires spring.security.core;
	requires spring.security.web;
	requires spring.security.config;
	requires spring.statemachine.core;
	requires spring.beans;
	requires spring.webflux;
	requires spring.webmvc;

	requires transitive io.reactivex.rxjava3;

	requires io.swagger.v3.oas.annotations;
	requires reactor.core;

	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.dataformat.csv;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.datatype.jdk8;
	requires com.fasterxml.jackson.datatype.jsr310;
	requires com.fasterxml.jackson.dataformat.javaprop;
	requires org.openapitools.jackson.nullable;

	requires org.apache.commons.lang3;
	requires static org.apache.tomcat.embed.core;
	requires org.slf4j;
	requires java.money;
	requires org.javamoney.moneta;

	requires net.finmath.lib;
	requires net.finmath.plots;
	requires org.jfree.jfreechart;

	requires nv.websocket.client;
	requires org.apache.httpcomponents.httpclient;
	requires org.apache.httpcomponents.httpcore;

	requires jakarta.xml.bind;
	requires jakarta.persistence;
	// requires jakarta.servlet;
}
