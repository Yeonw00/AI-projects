package com.example.news_summary.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.example.newssummary.dao.CoinLedger;
import com.example.newssummary.dao.LedgerType;
import com.example.newssummary.dao.User;
import com.example.newssummary.dao.UserBalance;
import com.example.newssummary.repository.CoinLedgerRepository;
import com.example.newssummary.repository.UserBalanceRepository;
import com.example.newssummary.service.payment.CoinLedgerService;

public class CoinLedgerServicdTest {
	
	private CoinLedgerRepository ledgerRepository;
	private UserBalanceRepository balanceRepository;
	private CoinLedgerService coinLedgerService;
	
	@BeforeEach
	void setUp() {
		ledgerRepository = Mockito.mock(CoinLedgerRepository.class);
		balanceRepository = Mockito.mock(UserBalanceRepository.class);
		coinLedgerService = new CoinLedgerService(ledgerRepository, balanceRepository);
		
		
	}
	
	@Test
	void testChargeIncreaseBalance() {
		Long userId = 1L;
		User user = new User();
		user.setId(userId);
		
		UserBalance balance = new UserBalance(user, 100L);
		
		Mockito.when(balanceRepository.findById(userId)).thenReturn(Optional.of(balance));
		Mockito.when(ledgerRepository.findByRequestId(Mockito.anyString())).thenReturn(Optional.empty());
		Mockito.when(balanceRepository.save(Mockito.any())).thenAnswer(i -> i.getArgument(0));
		Mockito.when(ledgerRepository.save(Mockito.any())).thenAnswer(i -> i.getArgument(0));
		
		// when
		CoinLedger ledger = coinLedgerService.createEntry(
				userId, 
				LedgerType.CHARGE, 
				300L, 
				"코인 충전", 
				"REQ-123", 
				"ORDER-123"
		);
		
		//then
		assertThat(balance.getBalance()).isEqualTo(400L);
		assertThat(ledger.getType()).isEqualTo(LedgerType.CHARGE);
		assertThat(ledger.getAmount()).isEqualTo(300L);
		assertThat(ledger.getBalanceAfter()).isEqualTo(400L);
	}
	
	@Test
	void testUseThrowsWhenInsufficientBalance() {
		Long userId = 2L;
		User user = new User();
		user.setId(userId);
		UserBalance balance = new UserBalance(user, 50L);
		
		Mockito.when(balanceRepository.findById(userId)).thenReturn(Optional.of(balance));
		
		assertThatThrownBy(() -> 
				coinLedgerService.createEntry(userId, LedgerType.USE, 100L, "요약 사용", "REQ-456", null)
		).isInstanceOf(IllegalStateException.class)
		.hasMessageContaining("INSUFFICIENT_BALANCE");
	}
	
	@Test
	void testIdempotentRequestReturnsExistingLedger() {
		Long userId = 3L;
		User user = new User();
		user.setId(userId);
		
		UserBalance balance = new UserBalance(user, 200L);
		CoinLedger existing = new CoinLedger();
		existing.setType(LedgerType.CHARGE);
		existing.setAmount(200L);
		existing.setBalanceAfter(400L);
		
		Mockito.when(balanceRepository.findById(userId)).thenReturn(Optional.of(balance));
		Mockito.when(ledgerRepository.findByRequestId("REQ-789")).thenReturn(Optional.of(existing));
		
		// when
		CoinLedger result = coinLedgerService.createEntry(
				userId, LedgerType.CHARGE, 200L, "중복요청", "REQ-789", "ORDER-789");
		
		// then
		assertThat(result).isSameAs(existing);
	}
}
