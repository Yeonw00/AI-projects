package com.example.newssummary.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.example.newssummary.dto.LedgerEntryResponse;
import com.example.newssummary.dto.LedgerPageResponse;
import com.example.newssummary.repository.UserBalanceRepository;
import com.example.newssummary.security.CustomUserDetails;
import com.example.newssummary.service.payment.CoinLedgerExcelExporter;
import com.example.newssummary.service.payment.CoinLedgerPdfExporter;
import com.example.newssummary.service.payment.CoinLedgerService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {
	
	@Autowired
	private UserBalanceRepository balanceRepository;
	
	@Autowired
	private CoinLedgerService coinLedgerService;
	
	@Autowired
	private CoinLedgerExcelExporter excelExporter;
	
	@Autowired
	private CoinLedgerPdfExporter pdfExporter;
	
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
	
	@GetMapping("/ledger/export")
	public void exportLedger(
			@AuthenticationPrincipal CustomUserDetails user,
			@RequestParam(required = false) LedgerType type,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
			@RequestParam(defaultValue = "EXCEL") String format,
			HttpServletResponse response
	) throws IOException {
		Long userId = user.getUser().getId();
		List<LedgerEntryResponse> rows = coinLedgerService.getUserLedgerAll(userId, type);
		
		if ("PDF".equalsIgnoreCase(format)) {
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=\"coin-ledger.pdf\"");
			pdfExporter.export(rows, response.getOutputStream());
		} else {
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-Disposition", "attachment; filename=\"coin-ledger.xlsx\"");
			excelExporter.export(rows, response.getOutputStream());
		}
	}
}


