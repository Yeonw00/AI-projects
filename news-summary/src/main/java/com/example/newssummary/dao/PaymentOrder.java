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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "payment_order")
public class PaymentOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true, nullable = false, length = 64)
	private String orderUid;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Column(length = 32, nullable = false)
	private String productCode; // COIN_1000 등
	
	private long coinAmount;
	private long price;
	
	@Enumerated(EnumType.STRING)
	@Column(length = 16, nullable = false)
	private OrderStatus status; // PENDING, PAID, FAILED, CANCELD
	
	private LocalDateTime createdAt;
	private LocalDateTime paidAt;
	
	
	public PaymentOrder(Long id, String orderUid, User user, String productCode, long coinAmount, long price,
			OrderStatus status, LocalDateTime createdAt, LocalDateTime paidAt) {
		super();
		this.id = id;
		this.orderUid = orderUid;
		this.user = user;
		this.productCode = productCode;
		this.coinAmount = coinAmount;
		this.price = price;
		this.status = status;
		this.createdAt = createdAt;
		this.paidAt = paidAt;
	}

	public PaymentOrder() {}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getOrderUid() {
		return orderUid;
	}


	public void setOrderUid(String orderUid) {
		this.orderUid = orderUid;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public String getProductCode() {
		return productCode;
	}


	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}


	public long getCoinAmount() {
		return coinAmount;
	}


	public void setCoinAmount(long coinAmount) {
		this.coinAmount = coinAmount;
	}


	public long getPrice() {
		return price;
	}


	public void setPrice(long price) {
		this.price = price;
	}


	public OrderStatus getStatus() {
		return status;
	}


	public void setStatus(OrderStatus status) {
		this.status = status;
	}


	public LocalDateTime getCreatedAt() {
		return createdAt;
	}


	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}


	public LocalDateTime getPaidAt() {
		return paidAt;
	}


	public void setPaidAt(LocalDateTime paidAt) {
		this.paidAt = paidAt;
	}
	
	@PrePersist
	public void onCreate() {
		if (createdAt == null) createdAt = LocalDateTime.now();
	}
}
