package com.example.newssummary.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.newssummary.dao.PaymentOrder;
import com.example.newssummary.dto.ConfirmRequest;
import com.example.newssummary.dto.ConfirmResponse;
import com.example.newssummary.dto.CreateOrderRequest;
import com.example.newssummary.dto.CreateOrderResponse;
import com.example.newssummary.dto.TossPaymentResponse;
import com.example.newssummary.repository.PaymentOrderRepository;
import com.example.newssummary.security.CustomUserDetails;
import com.example.newssummary.service.payment.OrderService;
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
		if (req.getPaymentKey() == null || req.getOrderId() == null || req.getAmount() == null) {
			return bad("INVALID_PARAM", "paymentKey/orderId/amount is required");
		}
		
		PaymentOrder order = orderRepository.findByOrderUid(req.getOrderId())
				.orElseThrow(() -> new IllegalArgumentException("order not found"));
		
		if (me != null && !order.getUser().getId().equals(me.getUser().getId())) {
			return forbidden("FORBIDDEN", "orderowner mismatch");
		}
		
		if ("PAID".equals(order.getStatus())) {
			return ResponseEntity.ok(new ConfirmResponse("PAID", order.getCoinAmount()));
		}
		
		Long price = order.getPrice();
		if (price == null) return bad("SERVER_DATA_ERROR", "order price is null");
		long orderAmount = price.longValue();
		long reqAmount = req.getAmount().longValue();
		if (orderAmount != reqAmount) {
			return bad("AMOUNT_MISMATCH", "server amount and request amount differ");
		}
		
		
		TossPaymentResponse res;
		try {
			res= tossClient.confirm(req.getPaymentKey(), req.getOrderId(), reqAmount);
		} catch (IllegalStateException ex) {
			return bad("TOSS_CONFIRM_ERROR", ex.getMessage());
		}
		
		Long totalAmount = (res == null) ? null : res.getTotalAmount();
		if(totalAmount == null ||  totalAmount.longValue() != orderAmount) {
			return bad("CONFIRM_FAILED", "toss totalAmount mismatch");
		}
		
		order.setStatus("PAID");
		order.setPaidAt(LocalDateTime.now());
		// TODO: paymenKey/승인번호/결제수단 저장
		orderRepository.save(order);
		
		walletService.grantChargeCoins(order);
		
		return ResponseEntity.ok(new ConfirmResponse("PAID", order.getCoinAmount()));
	}
	
	private ResponseEntity<?> bad(String code, String message) {
		return ResponseEntity.badRequest().body(java.util.Map.of("code", code, "message", message));
	}
	
	private ResponseEntity<?> forbidden(String code, String message) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(java.util.Map.of("code", code, "message", message));
	}
}
