package com.example.newssummary.dao;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "coin_ledger", indexes = {
		@Index(name = "idx_ledger_user_created", columnList = "user_id, createdAt")
})
public class CoinLedger {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Column(length = 32, nullable = false)
	private String type; // SIGNUP_BONUS, CHARGE, CONSUME_SUMMARY 등
	
	private long amount;
	private long balnaceAfter;
	
	@Column(length = 128)
	private String refKey; // 관련 주문 ID, 요약ID 등
	
	private LocalDateTime createdAt;

	public CoinLedger() {}
	
	public CoinLedger(Long id, User user, String type, long amount, long balnaceAfter, String refKey,
			LocalDateTime createdAt) {
		super();
		this.id = id;
		this.user = user;
		this.type = type;
		this.amount = amount;
		this.balnaceAfter = balnaceAfter;
		this.refKey = refKey;
		this.createdAt = createdAt;
	}
	
	public CoinLedger(User user, String type, long amount, long balnaceAfter, String refKey,
			LocalDateTime createdAt) {
		super();
		this.user = user;
		this.type = type;
		this.amount = amount;
		this.balnaceAfter = balnaceAfter;
		this.refKey = refKey;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public long getBalnaceAfter() {
		return balnaceAfter;
	}

	public void setBalnaceAfter(long balnaceAfter) {
		this.balnaceAfter = balnaceAfter;
	}

	public String getRefKey() {
		return refKey;
	}

	public void setRefKey(String refKey) {
		this.refKey = refKey;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	
	
	@PrePersist
	public void onCreate() {
		if (createdAt == null) createdAt = LocalDateTime.now();
	}
	
	
}
