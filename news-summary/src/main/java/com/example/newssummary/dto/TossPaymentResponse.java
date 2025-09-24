package com.example.newssummary.dto;

public class TossPaymentResponse {
	private String orderId;
	private String status;
	private String method;
	private Long totalAmount;
	
	public TossPaymentResponse() {}

	public TossPaymentResponse(String orderId, String status, String method, Long totalAmount) {
		super();
		this.orderId = orderId;
		this.status = status;
		this.method = method;
		this.totalAmount = totalAmount;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Long getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Long totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Override
	public String toString() {
		return "TossPaymentResponse [orderId=" + orderId + ", status=" + status + ", method=" + method
				+ ", totalAmount=" + totalAmount + "]";
	}
	
}