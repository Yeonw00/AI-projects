package com.example.newssummary.service.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.newssummary.dao.CoinLedger;
import com.example.newssummary.dao.LedgerType;
import com.example.newssummary.dao.OrderStatus;
import com.example.newssummary.dao.PaymentOrder;
import com.example.newssummary.dao.UserBalance;
import com.example.newssummary.exception.InsufficientCoinForRefundException;
import com.example.newssummary.repository.PaymentOrderRepository;
import com.example.newssummary.repository.UserBalanceRepository;

@Service
public class RefundService {
	
	@Autowired
	private PaymentOrderRepository orderRepository;
	
	@Autowired
	private UserBalanceRepository balanceRepository;
	
	@Autowired
	private CoinLedgerService coinLedgerService;
	
	@Autowired
	private TossClient tossClient;
	
	
	/**
     * 주문 UID 기준 환불 (전체)
     * @param orderUid   결제 고유 UID
     * @param requestId  멱등키(선택). 없으면 내부에서 생성
     * @param reason     로그용 설명(선택)
     */
	@Transactional
	public CoinLedger refundByOrderUid(String orderUid, String requestId, String reason) {
		PaymentOrder order = orderRepository.findByOrderUid(orderUid)
				.orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderUid));
		
		if(order.getStatus() == OrderStatus.REFUNDED) {
			throw new IllegalStateException("Order is already refunded=" + order.getStatus());
		}
		
		if (OrderStatus.PAID != order.getStatus()) {
			throw new IllegalStateException("Order is not PAID. current=" + order.getStatus());
		}
		
		Long userId = order.getUser().getId();
		long coinToTakeBack = order.getCoinAmount();
		
		UserBalance ub = balanceRepository.findById(userId)
				.orElseThrow(() -> new IllegalStateException("balance not found"));
		if(ub.getBalance() < coinToTakeBack) {
			throw new InsufficientCoinForRefundException(
					"Not enough coins to refund this order as-is");
		}
		
		// 1 외부 PG에 전액 환불 요청 (여기서는 생략/성공 가정)
		tossClient.cancelPayment(
				order.getPaymentKey(),
				reason
		);	
		
		// 2 코인 회수
		CoinLedger ledger = coinLedgerService.createEntry(
				userId, 
				LedgerType.REFUND, 
				coinToTakeBack, 
				"refund for order " + orderUid, 
				requestId, 
				orderUid
		);
		
		// 3 주문 상태 전이
		order.setStatus(OrderStatus.REFUNDED);
		orderRepository.save(order);
		
		return ledger;
	}
	
	/**
     * 주문 PK 기준 환불 (부분/전체)
     */
	@Transactional
	public CoinLedger refundByOrderId(Long orderId, @Nullable String requestId, String reason) {
		PaymentOrder order = orderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
		return refundByOrderUid(order.getOrderUid(), requestId, reason);
	}
	
}
