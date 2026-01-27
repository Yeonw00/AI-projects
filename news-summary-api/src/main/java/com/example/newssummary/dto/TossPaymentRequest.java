package com.example.newssummary.dto;

public class TossPaymentRequest {
	private String paymentKey;
	private String orderId;
	private long amount;
	
	public TossPaymentRequest() {}
	
	public TossPaymentRequest(String paymentKey, String orderId, long amount) {
		super();
		this.paymentKey = paymentKey;
		this.orderId = orderId;
		this.amount = amount;
	}

	public String getPaymentKey() {
		return paymentKey;
	}
	public void setPaymentKey(String paymentKey) {
		this.paymentKey = paymentKey;
	}
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	
	
}
