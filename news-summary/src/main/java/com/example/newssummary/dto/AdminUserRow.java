package com.example.newssummary.dto;

import java.time.LocalDateTime;

public class AdminUserRow {
	
	private Long id;
	private String email;
	private String userName;
	private String role;
	private boolean socialLogin;
	private long coinBalance;
	private LocalDateTime createdAt;
	
	public AdminUserRow() {}
	
	public AdminUserRow(Long id, String email, String userName, String role, boolean socialLogin, long coinBalance,
			LocalDateTime createdAt) {
		super();
		this.id = id;
		this.email = email;
		this.userName = userName;
		this.role = role;
		this.socialLogin = socialLogin;
		this.coinBalance = coinBalance;
		this.createdAt = createdAt;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public boolean isSocialLogin() {
		return socialLogin;
	}
	public void setSocialLogin(boolean socialLogin) {
		this.socialLogin = socialLogin;
	}
	public long getCoinBalance() {
		return coinBalance;
	}
	public void setCoinBalance(long coinBalance) {
		this.coinBalance = coinBalance;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
