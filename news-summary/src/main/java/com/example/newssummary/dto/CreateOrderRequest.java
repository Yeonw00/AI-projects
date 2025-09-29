package com.example.newssummary.dto;

public class CreateOrderRequest {
	private String productCode;
	
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	
	@Override
	public String toString() {
		return "CreateOrderRequest [productCode=" + productCode + "]";
	}
}
