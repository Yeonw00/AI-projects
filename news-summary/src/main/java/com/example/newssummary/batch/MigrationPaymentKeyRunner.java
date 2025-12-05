package com.example.newssummary.batch;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.newssummary.dao.OrderStatus;
import com.example.newssummary.dao.PaymentOrder;
import com.example.newssummary.dto.TossPaymentResponse;
import com.example.newssummary.repository.PaymentOrderRepository;
import com.example.newssummary.service.payment.TossClient;

@Component
@Profile("payment-migrate")
public class MigrationPaymentKeyRunner implements CommandLineRunner {

	@Autowired
	private PaymentOrderRepository orderRepository;
	
	@Autowired
	private TossClient tossClient;

	public MigrationPaymentKeyRunner(PaymentOrderRepository orderRepository, TossClient tossClient) {
		this.orderRepository = orderRepository;
		this.tossClient = tossClient;
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("=== [Migration] paymentKey 채우기 시작 ===");
		
		List<PaymentOrder> targets =
				orderRepository.findByPaymentKeyIsNullAndStatus(OrderStatus.PAID);
		
		System.out.println("총 대상 건수: " + targets.size());
		
		int success = 0;
		int fail = 0;
		
		for (PaymentOrder order : targets) {
			String orderId = order.getOrderUid();
			
			TossPaymentResponse res = tossClient.getPaymentByOrderId(orderId);
			
			if (res == null || res.getPaymentKey() == null) {
				System.out.println("실패: orderId=" + orderId);
				fail++;
				continue;
			}
			
			order.setPaymentKey(res.getPaymentKey());
			orderRepository.save(order);
			System.out.println("성공: orderId=" + orderId + " / paymentKey=" + res.getPaymentKey());
			success++;
		}
		
		System.out.println("=== [완료] 성공: " + success + " / 실패: " + fail + "===");
	}
}
