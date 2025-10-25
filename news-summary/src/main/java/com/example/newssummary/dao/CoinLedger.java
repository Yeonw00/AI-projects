package com.example.newssummary.dao;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
		@Index(name = "idx_ledger_user_created", columnList = "user_id, createdAt"),
		@Index(name = "idx_ledger_order_id", columnList = "orderId")
})
public class CoinLedger {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 32, nullable = false)
	private LedgerType type; // SIGNUP_BONUS, CHARGE, CONSUME_SUMMARY 등
	
	@Column(nullable = false)
	private long amount;
	
	@Column(name = "balance_after", nullable = false)
	private long balanceAfter;
	
	@Column(length = 128)
	private String orderId; // 업무 연계 키(결제주문ID, 요약작업ID 등)
	
	private LocalDateTime createdAt;
	
	@Column(length = 255)
	private String description; // UI/관리자용 메모
	
	@Column(length = 64, unique = true)
    private String requestId;     // 멱등키(결제/웹훅 등), NULL 허용

	public CoinLedger() {}
	
	public CoinLedger(Long id, User user, LedgerType type, long amount, long balanceAfter, String orderId,
			LocalDateTime createdAt) {
		super();
		this.id = id;
		this.user = user;
		this.type = type;
		this.amount = amount;
		this.balanceAfter = balanceAfter;
		this.orderId = orderId;
		this.createdAt = createdAt;
	}
	
	public CoinLedger(User user, LedgerType type, long amount, long balanceAfter, String orderId,
			LocalDateTime createdAt) {
		super();
		this.user = user;
		this.type = type;
		this.amount = amount;
		this.balanceAfter = balanceAfter;
		this.orderId = orderId;
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

	public LedgerType getType() {
		return type;
	}

	public void setType(LedgerType type) {
		this.type = type;
	}

	public long getAmount() {
		return amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	public long getBalanceAfter() {
		return balanceAfter;
	}

	public void setBalanceAfter(long balanceAfter) {
		this.balanceAfter = balanceAfter;
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
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	@PrePersist
	public void onCreate() {
		if (createdAt == null) createdAt = LocalDateTime.now();
	}
	
	
}
