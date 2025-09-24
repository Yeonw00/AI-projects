package com.example.newssummary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.newssummary.dao.UserBalance;
import com.example.newssummary.dto.BalanceResponse;
import com.example.newssummary.repository.UserBalanceRepository;
import com.example.newssummary.security.CustomUserDetails;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {
	
	@Autowired
	private UserBalanceRepository balanceRepository;
	
	@GetMapping("/me")
	public ResponseEntity<?> getBalance(@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long userId = userDetails.getUser().getId();
		UserBalance ub = balanceRepository.findById(userId).orElse(null);
		long balance = ub == null ? 0 : ub.getBalance();
		return ResponseEntity.ok(new BalanceResponse(balance));
	}
}
