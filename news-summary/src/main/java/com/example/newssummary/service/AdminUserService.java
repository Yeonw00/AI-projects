package com.example.newssummary.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.newssummary.dao.UserBalance;
import com.example.newssummary.dto.AdminUserRow;
import com.example.newssummary.repository.UserBalanceRepository;
import com.example.newssummary.repository.UserRepository;

@Service
public class AdminUserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserBalanceRepository balanceRepository;
	
	public AdminUserService(UserRepository userRepository, UserBalanceRepository balanceRepository) {
		super();
		this.userRepository = userRepository;
		this.balanceRepository = balanceRepository;
	}

	@Transactional(readOnly = true)
	public List<AdminUserRow> getAllUsers() {
		var users = userRepository.findAll();
		
		return users.stream().map(u -> {
			long balance = balanceRepository.findById(u.getId())
					.map(UserBalance::getBalance)
					.orElse(0L);
			
			return new AdminUserRow(
					u.getId(),
					u.getEmail(),
					u.getUsername(),
					u.getRole(),
					u.getPasswordHash() == null,
					balance,
					u.getCreatedAt()
			);
		}).toList();
	}

}
