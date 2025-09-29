package com.example.newssummary.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.newssummary.dao.PaymentOrder;
import com.example.newssummary.dto.ConfirmReqeust;
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
	public ResponseEntity<?> confirm(@RequestBody ConfirmReqeust req) {
		PaymentOrder order = orderRepository.findByOrderUid(req.getOrderId())
				.orElseThrow(() -> new IllegalArgumentException("order not found"));
		
		if(order.getPrice() != req.getAmount()) {
			return ResponseEntity.badRequest().body("amount mismatch");
		}
		
		TossPaymentResponse res;
		try {
			res= tossClient.confirm(req.getPaymentKey(), req.getOrderId(), req.getAmount());
		} catch (IllegalStateException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
		
		if (res == null || res.getTotalAmount() == null || 
				res.getTotalAmount() != order.getPrice()) {
			return ResponseEntity.badRequest().body("confirm failed or amount mismatch");
		}
		
		order.setStatus("PAID");
		order.setPaidAt(LocalDateTime.now());
		orderRepository.save(order);
		
		walletService.grantChargeCoins(order);
		
		return ResponseEntity.ok(new ConfirmResponse("PAID", order.getCoinAmount()));
	}
	
}
