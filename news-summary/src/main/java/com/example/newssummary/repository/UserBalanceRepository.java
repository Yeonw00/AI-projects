package com.example.newssummary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.newssummary.dao.UserBalance;

public interface UserBalanceRepository  extends JpaRepository<UserBalance, Long>{
	UserBalance findByUserId(Long userId);
}
