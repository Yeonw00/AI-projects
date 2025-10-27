package com.example.newssummary.service.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.newssummary.dao.CoinLedger;
import com.example.newssummary.dao.LedgerType;
import com.example.newssummary.dao.PaymentOrder;
import com.example.newssummary.repository.PaymentOrderRepository;

@Service
public class RefundService {
	
	@Autowired
	private PaymentOrderRepository paymentOrderRepository;
	
	@Autowired
	private CoinLedgerService coinLedgerService;
	
	
	/**
     * 주문 UID 기준 환불 (부분/전체)
     * @param orderUid   결제 고유 UID
     * @param coins      환불 코인 수량 (null 또는 0 이하 => 전체 환불로 간주)
     * @param requestId  멱등키(선택). 없으면 내부에서 생성
     * @param reason     로그용 설명(선택)
     */
	@Transactional
	public CoinLedger refundByOrderUid(String orderUid,
										@Nullable Long coins,
										@Nullable String requestId,
										@Nullable String reason) {
		PaymentOrder order = paymentOrderRepository.findByOrderUid(orderUid)
				.orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderUid));
		
		if (!"PAID".equals(order.getStatus())) {
			throw new IllegalStateException("Order is not PAID. current=" + order.getStatus());
		}
		
		long refundable = order.getCoinAmount();
		long refundAmount = (coins == null || coins <=0) ? refundable : coins;
		
		if (refundAmount <= 0) {
			throw new IllegalArgumentException("Refund amount must be positive");
		}
		
		if (refundAmount > refundable) {
			throw new IllegalArgumentException("Refund amount exceeds paid coins. paid=" + refundable + "req=" + refundAmount);
		}
		
		String desc = (reason != null && !reason.isBlank()) ? reason : "결제 환불";
		String rid = (requestId != null && !requestId.isBlank())
				? requestId
				: "REFUND-" + orderUid + "-" + refundAmount;
		
		// 코인 활불 + 레저 기록 (잔액 갱신 포함)
		CoinLedger ledger = coinLedgerService.createEntry(
				order.getUser().getId(), 
				LedgerType.REFUND, 
				refundAmount, 
				desc, 
				rid, 
				String.valueOf(order.getId())
		);
		
		// 전체 환불이면 상태 갱신
		if (refundAmount == refundable) {
			order.setStatus("REFUNDED");
			paymentOrderRepository.save(order);
		}
		
		return ledger;
	}
	
	/**
     * 주문 PK 기준 환불 (부분/전체)
     */
	@Transactional
	public CoinLedger refundByOrderId(Long orderId,
										@Nullable Long coins,
										@Nullable String requestId,
										@Nullable String reason) {
		PaymentOrder order = paymentOrderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
		return refundByOrderUid(order.getOrderUid(), coins, requestId, reason);
	}
	
}
