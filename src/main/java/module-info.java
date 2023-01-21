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
	exports net.finmath.smartcontract.marketdata.util;
	exports net.finmath.smartcontract.marketdata.curvecalibration;
	exports net.finmath.smartcontract.service.utils;
	exports net.finmath.smartcontract.marketdata.adapters;

	opens net.finmath.smartcontract.client;
	opens net.finmath.smartcontract.product.xml;
	opens net.finmath.smartcontract.service to java.base, spring.core;
	opens net.finmath.smartcontract.service.config to spring.core;
	opens net.finmath.smartcontract.webflux to spring.core;
	exports net.finmath.smartcontract.webflux;
    exports net.finmath.smartcontract.reactive;




    requires java.datatransfer;
	requires java.logging;
	requires java.validation;
	requires java.annotation;
	requires java.desktop;


	requires javafx.swing;
	requires javafx.graphics;
	requires javafx.controls;

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
	requires spring.webflux;


	requires io.swagger.v3.oas.annotations;
	requires reactor.core;

	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.dataformat.csv;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.datatype.jdk8;
	requires com.fasterxml.jackson.datatype.jsr310;
	requires com.fasterxml.jackson.dataformat.javaprop;
	requires jackson.databind.nullable;

	requires org.apache.commons.lang3;
	requires org.apache.tomcat.embed.core;
	requires org.slf4j;
	requires money.api;
	requires moneta;

	requires net.finmath.lib;
	requires net.finmath.plots;
	requires org.jfree.jfreechart;

	requires nv.websocket.client;
	requires org.apache.httpcomponents.httpclient;
	requires org.apache.httpcomponents.httpcore;

	requires io.reactivex.rxjava3;



}