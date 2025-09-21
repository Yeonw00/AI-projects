package com.example.newssummary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.newssummary.dao.User;
import com.example.newssummary.dao.UserBalance;
import com.example.newssummary.dto.BalanceResponse;
import com.example.newssummary.repository.CoinLedgerRepository;
import com.example.newssummary.repository.UserBalanceRepository;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {
	
	@Autowired
	private UserBalanceRepository balanceRepository;
	
	@PostMapping("/balance")
	public ResponseEntity<?> getBalance(@RequestBody User user) {
		UserBalance ub = balanceRepository.findById(user.getId()).orElse(null);
		long balance = ub == null ? 0 : ub.getBalance();
		return ResponseEntity.ok(new BalanceResponse(balance));
	}
}
