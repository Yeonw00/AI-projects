package com.example.newssummary.exception;

public class InsufficientCoinForRefundException extends RuntimeException {

	public InsufficientCoinForRefundException() {
		super("Not enough coins to refund this order.");
	}
	
	public InsufficientCoinForRefundException(String message) {
		super(message);
	}
}
