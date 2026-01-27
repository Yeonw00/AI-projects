package com.example.newssummary.dto;

public class BalanceResponse {
	private long balance;

	public BalanceResponse(long balance) {
		super();
		this.balance = balance;
	}

	public long getBalance() {
		return balance;
	}

	public void setBalance(long balance) {
		this.balance = balance;
	}

	@Override
	public String toString() {
		return "BalanceResponse [balance=" + balance + "]";
	}
}
