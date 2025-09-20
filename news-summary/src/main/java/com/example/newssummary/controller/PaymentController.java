package com.example.newssummary.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import com.example.newssummary.repository.PaymentOrderRepository;
import com.example.newssummary.service.payment.OrderService;
import com.example.newssummary.service.payment.TossClient;
import com.example.newssummary.service.payment.WalletService;
import com.example.newssummary.service.payment.TossClient.TossConfirmResponse;

import reactor.core.publisher.Mono;

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
	public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest req) {
		PaymentOrder order = orderService.createPendingOrder(req.getUserId(), req.getProductCode());
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
		
		TossConfirmResponse res = tossClient.confirm(req.getPaymentKey(), req.getOrderId(), req.getAmount()).block();
		if (res == null || res.totalAmount() == null || res.totalAmount() != order.getPrice()) {
			return ResponseEntity.badRequest().body("confirm failed or amount mismatch");
		}
		
		order.setStatus("PAID");
		order.setPaidAt(LocalDateTime.now());
		orderRepository.save(order);
		
		walletService.grantChargeCoins(order);
		
		return ResponseEntity.ok(new ConfirmResponse("PAID", order.getCoinAmount()));
	}
	
}
