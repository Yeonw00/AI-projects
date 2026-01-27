package com.example.newssummary.dto;

import java.time.LocalDateTime;

import com.example.newssummary.dao.LedgerType;

public class LedgerEntryResponse {
	private Long id;
    private LedgerType type;
    private Long amount;           // +양수
    private Long balanceAfter;
    private String description;
    private String orderId;
    private LocalDateTime createdAt;
    
    public LedgerEntryResponse(){}

	public LedgerEntryResponse(Long id, LedgerType type, Long amount, Long balanceAfter, String description,
			String orderId, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.type = type;
		this.amount = amount;
		this.balanceAfter = balanceAfter;
		this.description = description;
		this.orderId = orderId;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LedgerType getType() {
		return type;
	}

	public void setType(LedgerType type) {
		this.type = type;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	public Long getBalanceAfter() {
		return balanceAfter;
	}

	public void setBalanceAfter(Long balanceAfter) {
		this.balanceAfter = balanceAfter;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	};
	
	
}
