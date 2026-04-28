package com.bank.bank_api.controller;

import com.bank.bank_api.service.BankService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/bank")
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping("/balance")
    public ResponseEntity<BankService.ApiResponse> getBalance(@RequestParam Long userId) {
        BankService.ApiResponse response = bankService.getBalance(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/put")
    public ResponseEntity<BankService.ApiResponse> putMoney(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        BankService.ApiResponse response = bankService.putMoney(userId, amount);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/take")
    public ResponseEntity<BankService.ApiResponse> takeMoney(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        BankService.ApiResponse response = bankService.takeMoney(userId, amount);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/operations")
    public ResponseEntity<BankService.ApiResponse> getOperationList(@RequestParam Long userId,
                                                                    @RequestParam (required = false) String startDate,
                                                                    @RequestParam (required = false) String endDate) {
        BankService.ApiResponse response = bankService.getOperationList(userId, startDate, endDate);
        return ResponseEntity.ok(response);
    }
}
