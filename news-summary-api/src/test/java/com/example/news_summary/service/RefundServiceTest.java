package com.example.news_summary.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.newssummary.dao.OrderStatus;
import com.example.newssummary.dao.PaymentOrder;
import com.example.newssummary.dao.User;
import com.example.newssummary.repository.PaymentOrderRepository;
import com.example.newssummary.repository.UserBalanceRepository;
import com.example.newssummary.service.payment.CoinLedgerService;
import com.example.newssummary.service.payment.RefundService;

@ExtendWith(MockitoExtension.class)
public class RefundServiceTest {
	
	@Mock PaymentOrderRepository orderRepo;
	@Mock UserBalanceRepository balanceRepo;
	@Mock CoinLedgerService coinLedgerService;
	
	@InjectMocks RefundService refundService;
	
	private final Long USER_ID = 10L;
	private final String ORDER_ID = "ORD-1001";
	
	@Test
	void refund_success_updatesOrder_and_Balance_and_Ledger() {
		PaymentOrder order = new PaymentOrder();
		order.setOrderUid(ORDER_ID);
		User user = new User();
		user.setId(USER_ID);
		order.setUser(user);
		order.setStatus(OrderStatus.PAID);
		order.setPrice(10000);
		
	}
}
