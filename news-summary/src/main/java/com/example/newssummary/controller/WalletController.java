package com.example.newssummary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.newssummary.dao.LedgerType;
import com.example.newssummary.dao.UserBalance;
import com.example.newssummary.dto.BalanceResponse;
import com.example.newssummary.dto.LedgerPageResponse;
import com.example.newssummary.repository.UserBalanceRepository;
import com.example.newssummary.security.CustomUserDetails;
import com.example.newssummary.service.payment.CoinLedgerService;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {
	
	@Autowired
	private UserBalanceRepository balanceRepository;
	
	@Autowired
	private CoinLedgerService coinLedgerService;
	
	@GetMapping("/me")
	public ResponseEntity<?> getBalance(@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long userId = userDetails.getUser().getId();
		UserBalance ub = balanceRepository.findById(userId).orElse(null);
		long balance = ub == null ? 0 : ub.getBalance();
		return ResponseEntity.ok(new BalanceResponse(balance));
	}
	
	@GetMapping("/ledger")
	public ResponseEntity<LedgerPageResponse> getLedger(
			@RequestParam(name="type", required=false) LedgerType type,
			@RequestParam(name="page", defaultValue="0") int page,
			@RequestParam(name="size", defaultValue="20") int size
	) {
		Long userId = getCurrentUserIdOrThrow();
		LedgerPageResponse resp = coinLedgerService.getUserLedger(userId, type, page, size);
		return ResponseEntity.ok(resp);
	}
	
	private Long getCurrentUserIdOrThrow() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails d)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}
		return d.getUser().getId();
	}
}
