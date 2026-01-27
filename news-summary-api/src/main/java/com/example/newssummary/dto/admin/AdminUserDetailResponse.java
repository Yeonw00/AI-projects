package com.example.newssummary.dto.admin;

import java.time.LocalDateTime;

public record AdminUserDetailResponse(
		String email,
		String username,
		LocalDateTime createdAt,
		Long coinBalance,
		long totalRequestCount,
		LocalDateTime lastActivityAt,
		long successCount,
		long totalSpentBalance
) {}
