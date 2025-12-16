package com.example.newssummary.service.payment;

import java.util.ArrayList;
import java.util.List;

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
import com.example.newssummary.repository.CoinLedgerRepository;
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
	private CoinLedgerRepository ledgerRepository;
	
	@Autowired
	private TossClient tossClient;
	
	
	@Transactional(readOnly = true)
	public List<PaymentOrder> getRefundableOrders(Long userId) {
		long balance = balanceRepository.findById(userId)
				.map(UserBalance::getBalance)
				.orElse(0L);
		
		List<PaymentOrder> paidOrders =
				orderRepository.findByUserIdAndStatusOrderByPaidAtDesc(userId, OrderStatus.PAID);
		
		List<PaymentOrder> result = new ArrayList<>();
		long remaining = balance;
		
		for (PaymentOrder o : paidOrders) {
			long coins = o.getCoinAmount();
			
			if (remaining >= coins) {
				result.add(o);
				remaining -= coins;
			} else {
				break;
			}
		}
		
		return result;
	}
	

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
		
		Long orderId = order.getId();
		
		if(requestId == null || requestId.isBlank()) {
			requestId = orderUid;
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
		
		// 2 DB 상태 업데이트
		order.setStatus(OrderStatus.REFUNDED);
		orderRepository.save(order);
		
		// 3 ledger 기록(createEntry에서 코인차감)
		CoinLedger ledger = coinLedgerService.createEntry(
				userId, 
				LedgerType.REFUND, 
				coinToTakeBack, 
				reason,
				requestId, 
				String.valueOf(orderId)
		);
		
		
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
