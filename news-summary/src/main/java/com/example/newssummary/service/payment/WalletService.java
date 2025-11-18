package com.example.newssummary.service.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.newssummary.dao.CoinLedger;
import com.example.newssummary.dao.LedgerType;
import com.example.newssummary.dao.OrderStatus;
import com.example.newssummary.dao.PaymentOrder;

import jakarta.transaction.Transactional;

@Service
public class WalletService {

	@Autowired
	private CoinLedgerService coinLedgerService;
	
	
	@Transactional
	public long grantChargeCoins(PaymentOrder order) {
		
		if(!OrderStatus.PAID.equals(order.getStatus())) {
			throw new IllegalStateException("order is not PAID");
		}
		
		long coins = order.getCoinAmount();
		if (coins <= 0) {
			return 0L;
		}
		
		Long userId = order.getUser().getId();
		
		CoinLedger ledger = coinLedgerService.createEntry(
				userId,
				LedgerType.CHARGE,
				coins,
				"코인 충전 (" + order.getProductCode() + ")", // description
				order.getOrderUid(),   // 멱등키 (중복 방지)
				String.valueOf(order.getId())     // 결제 주문 ID
		);
		
		return ledger.getAmount();
	}
	
}
