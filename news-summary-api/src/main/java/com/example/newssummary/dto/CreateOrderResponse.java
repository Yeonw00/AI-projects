package com.example.newssummary.dto;

public class CreateOrderResponse {
	private String orderId;
	private long amount;
	
	public CreateOrderResponse() {}
	
	public CreateOrderResponse(String orderId, long amount) {
		super();
		this.orderId = orderId;
		this.amount = amount;
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
	
	@Override
	public String toString() {
		return "CreateOrderResponse [orderId=" + orderId + ", amount=" + amount + "]";
	}
}
