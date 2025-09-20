package com.example.newssummary.dto;

public class CreateOrderRequest {
	private Long userId;
	private String productCode;
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	
	@Override
	public String toString() {
		return "CreateOrderRequest [userId=" + userId + ", productCode=" + productCode + "]";
	}
}
