package net.finmath.smartcontract.valuation.service.controllers;

import net.finmath.smartcontract.api.SettlementApi;
import net.finmath.smartcontract.model.InitialSettlementRequest;
import net.finmath.smartcontract.model.InitialSettlementResult;
import net.finmath.smartcontract.model.RegularSettlementRequest;
import net.finmath.smartcontract.model.RegularSettlementResult;
import net.finmath.smartcontract.valuation.service.utils.SettlementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SettlementController implements SettlementApi {

	private static final Logger logger = LoggerFactory.getLogger(SettlementController.class);
	private final SettlementService settlementService;

	public SettlementController(SettlementService settlementService) {this.settlementService = settlementService;}

	@Override
	public ResponseEntity<RegularSettlementResult> generateRegularSettlementResult(RegularSettlementRequest regularSettlementRequest) {
		logger.info("Generating regular settlement result, generateRegularSettlementResult");
		RegularSettlementResult regularSettlementResult = settlementService.generateRegularSettlementResult(regularSettlementRequest);
		return ResponseEntity.ok(regularSettlementResult);
	}

	@Override
	public ResponseEntity<InitialSettlementResult> generateInitialSettlementResult(InitialSettlementRequest initialSettlementRequest) {
		logger.info("Generating initial settlement result, generateInitialSettlementResult");
		InitialSettlementResult initialSettlementResult = settlementService.generateInitialSettlementResult(initialSettlementRequest);
		return ResponseEntity.ok(initialSettlementResult);
	}
}
