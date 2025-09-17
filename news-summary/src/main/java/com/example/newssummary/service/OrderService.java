package com.example.newssummary.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.newssummary.dao.PaymentOrder;
import com.example.newssummary.dao.User;
import com.example.newssummary.repository.PaymentOrderRepository;
import com.example.newssummary.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

	@Autowired
	private PaymentOrderRepository orderRepository;
	
	@Autowired
	private UserRepository userRepository;

	public OrderService(PaymentOrderRepository orderRepository) {
		super();
		this.orderRepository = orderRepository;
	}
	
	@Transactional
	public PaymentOrder createPendingOrder(Long userId, String productCode) {
		var item = ProductCatalog.ITEMS.get(productCode);
		if (item == null) throw new IllegalArgumentException("Unknown product");
		
		String orderUid = "ORD_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
		
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		
		PaymentOrder order = new PaymentOrder();
		order.setOrderUid(orderUid);
		order.setUser(user);
		order.setProductCode(productCode);
		order.setCoinAmount(item.coin());
		order.setPrice(item.price());
		order.setStatus("PENDING");
		
		return orderRepository.save(order);
	}
}
