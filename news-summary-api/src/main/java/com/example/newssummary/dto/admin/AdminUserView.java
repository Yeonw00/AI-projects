package com.example.newssummary.dto.admin;

import java.time.LocalDateTime;

public interface AdminUserView {
	Long getId();
	String getEmail();
	String getUsername();
	String getRole();
	Boolean getSocialLogin();
	Long getCoinBalance();
	LocalDateTime getCreatedAt();
}
