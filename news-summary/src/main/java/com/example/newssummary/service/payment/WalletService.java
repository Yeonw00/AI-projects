package com.example.newssummary.service.payment;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.newssummary.dao.CoinLedger;
import com.example.newssummary.dao.LedgerType;
import com.example.newssummary.dao.PaymentOrder;
import com.example.newssummary.dao.User;
import com.example.newssummary.dao.UserBalance;
import com.example.newssummary.repository.CoinLedgerRepository;
import com.example.newssummary.repository.PaymentOrderRepository;
import com.example.newssummary.repository.UserBalanceRepository;

import jakarta.transaction.Transactional;

@Service
public class WalletService {

	@Autowired
	private UserBalanceRepository balanceRepository;
	
	@Autowired
	private CoinLedgerRepository ledgerRepository;
	
	@Autowired
	private CoinLedgerService coinLedgerService;
	
	
	@Transactional
	public void grantChargeCoins(PaymentOrder order) {
		if(!"PAID".equals(order.getStatus())) {
			throw new IllegalStateException("order is not PAID");
		}
		User user = order.getUser();
		Long userId = user.getId();
		
		UserBalance ub = balanceRepository.findById(userId)
				.orElse(new UserBalance(user, 0L));
		
		ub.increse(order.getCoinAmount());
		balanceRepository.save(ub);
		
		ledgerRepository.save(new CoinLedger(
					user, 
					LedgerType.CHARGE,
					order.getCoinAmount(),
					ub.getBalance(),
					order.getOrderUid(),
					LocalDateTime.now()));
		
		
		coinLedgerService.createEntry(
				userId,
				LedgerType.CHARGE,
				order.getCoinAmount(),
				"코인 충전 (" + order.getProductCode() + ")", // description
				order.getOrderUid(),   // 멱등키 (중복 방지)
				String.valueOf(order.getId())     // 결제 주문 ID
		);
	}
	
}
