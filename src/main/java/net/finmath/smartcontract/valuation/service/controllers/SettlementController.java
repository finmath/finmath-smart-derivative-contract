package net.finmath.smartcontract.valuation.service.controllers;

import net.finmath.smartcontract.api.SettlementApi;
import net.finmath.smartcontract.model.InitialSettlementRequest;
import net.finmath.smartcontract.model.InitialSettlementResult;
import net.finmath.smartcontract.model.RegularSettlementRequest;
import net.finmath.smartcontract.model.RegularSettlementResult;
import net.finmath.smartcontract.valuation.service.utils.SettlementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SettlementController implements SettlementApi {

	private final SettlementService settlementService;

	public SettlementController(SettlementService settlementService) {this.settlementService = settlementService;}

	@Override
	public ResponseEntity<RegularSettlementResult> generateRegularSettlementResult(RegularSettlementRequest regularSettlementRequest) {
		RegularSettlementResult regularSettlementResult = settlementService.generateRegularSettlementResult(regularSettlementRequest);
		return ResponseEntity.ok(regularSettlementResult);
	}

	@Override
	public ResponseEntity<InitialSettlementResult> generateInitialSettlementResult(InitialSettlementRequest initialSettlementRequest) {
		InitialSettlementResult initialSettlementResult = settlementService.generateInitialSettlementResult(initialSettlementRequest);
		return ResponseEntity.ok(initialSettlementResult);
	}
}
