package com.example.newssummary.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.newssummary.dao.CoinLedger;
import com.example.newssummary.dao.OrderStatus;
import com.example.newssummary.dao.PaymentOrder;
import com.example.newssummary.dto.ConfirmRequest;
import com.example.newssummary.dto.ConfirmResponse;
import com.example.newssummary.dto.CreateOrderRequest;
import com.example.newssummary.dto.CreateOrderResponse;
import com.example.newssummary.dto.RefundableOrderResponse;
import com.example.newssummary.dto.TossPaymentResponse;
import com.example.newssummary.repository.PaymentOrderRepository;
import com.example.newssummary.security.CustomUserDetails;
import com.example.newssummary.service.payment.OrderService;
import com.example.newssummary.service.payment.RefundService;
import com.example.newssummary.service.payment.TossClient;
import com.example.newssummary.service.payment.WalletService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private PaymentOrderRepository orderRepository;
	
	@Autowired
	private TossClient tossClient;
	
	@Autowired
	private WalletService walletService;
	
	@Autowired
	private RefundService refundService;
	
	@PostMapping("/orders")
	public ResponseEntity<?> createOrder(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody CreateOrderRequest req) {
		Long userId = userDetails.getUser().getId();
		PaymentOrder order = orderService.createPendingOrder(userId, req.getProductCode());
		return ResponseEntity.ok(new CreateOrderResponse(order.getOrderUid(), order.getPrice()));
	}
	
	@PostMapping("/confirm")
	@Transactional
	public ResponseEntity<?> confirm(@AuthenticationPrincipal CustomUserDetails me, 
									@RequestBody ConfirmRequest req) {
		// 1. 기본 파라미터 검증
		if (req.getPaymentKey() == null || req.getOrderId() == null || req.getAmount() == null) {
			return bad("INVALID_PARAM", "paymentKey/orderId/amount is required");
		}
		
		// 2. 주문 조회
		PaymentOrder order = orderRepository.findByOrderUid(req.getOrderId())
				.orElseThrow(() -> new IllegalArgumentException("order not found"));
		
		
		// 3. 주문 소유자 검증
		if (me != null && !order.getUser().getId().equals(me.getUser().getId())) {
			return forbidden("FORBIDDEN", "orderowner mismatch");
		}
		
		// 4. 멱등 처리: 이미 결제 완료된 주문
		if (OrderStatus.PAID.equals(order.getStatus())) {
			return ResponseEntity.ok(new ConfirmResponse("PAID", order.getCoinAmount()));
		}
		
		// 5. 승인 가능한 상태(PENDING)인지 확인
		if (order.getStatus() != OrderStatus.PENDING) {
			return bad("INVALID_STATE", "order must be PENDING to confrim. current=" + order.getStatus());
		}
		
		// 6. 금액 검증
		Long price = order.getPrice();
		if (price == null) return bad("SERVER_DATA_ERROR", "order price is null");
		long orderAmount = price.longValue();
		long reqAmount = req.getAmount().longValue();
		if (orderAmount != reqAmount) {
			return bad("AMOUNT_MISMATCH", "server amount and request amount differ");
		}
		
		// 7. Toss 결제 승인 요청 (여기서 PENDING -> PAID)
		TossPaymentResponse res;
		try {
			res= tossClient.confirm(req.getPaymentKey(), req.getOrderId(), reqAmount);
		} catch (IllegalStateException ex) {
			return bad("TOSS_CONFIRM_ERROR", ex.getMessage());
		}
		
		// 8. Toss 응답 검증
		Long totalAmount = (res == null) ? null : res.getTotalAmount();
		if(totalAmount == null ||  totalAmount.longValue() != orderAmount) {
			return bad("CONFIRM_FAILED", "toss totalAmount mismatch");
		}
		
		// 9. 결제 완료 처리
		order.setStatus(OrderStatus.PAID);
		order.setPaidAt(LocalDateTime.now());
		// TODO: paymenKey/승인번호/결제수단 저장
		orderRepository.save(order);
		
		// 10. 코인 지급
		long coinsAdded = walletService.grantChargeCoins(order);
		
		// 11. 응답 반환
		return ResponseEntity.ok(new ConfirmResponse("PAID", coinsAdded));
	}
	
	private ResponseEntity<?> bad(String code, String message) {
		return ResponseEntity.badRequest().body(java.util.Map.of("code", code, "message", message));
	}
	
	private ResponseEntity<?> forbidden(String code, String message) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(java.util.Map.of("code", code, "message", message));
	}
	
	@GetMapping("/refundable")
	public List<RefundableOrderResponse> getRefundableOrders(@AuthenticationPrincipal CustomUserDetails userDetails) {
		Long userId = userDetails.getUser().getId();
		List<PaymentOrder> orders = refundService.getRefundableOrders(userId);
		return orders.stream()
				.map(RefundableOrderResponse::from)
				.toList();
	}

	
	@PostMapping("/refund/{orderUid}")
	@Transactional
	public ResponseEntity<?> refundByOrderUid(@PathVariable String orderUid,
												@RequestParam(required = false) Long coins,
												@RequestParam(required = false) String requestId,
												@RequestParam(required = false) String reason) {
		CoinLedger ledger = refundService.refundByOrderUid(orderUid, requestId, reason);
		return ResponseEntity.ok(ledger.getId());
	}
}
