package com.example.newssummary.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.newssummary.dto.AdminUserRow;
import com.example.newssummary.repository.UserBalanceRepository;
import com.example.newssummary.repository.UserRepository;
import com.example.newssummary.repository.admin.AdminUserRepository;

@Service
public class AdminUserService {
	
	@Autowired
	private AdminUserRepository adminUserRepository;
	
	public AdminUserService(AdminUserRepository adminUserRepository) {
		super();
		this.adminUserRepository = adminUserRepository;
	}

	@Transactional(readOnly = true)
	public List<AdminUserRow> getAllUsers() {
		return adminUserRepository.findAdminUserList()
                .stream()
                .map(v -> new AdminUserRow(
                        v.getId(),
                        v.getEmail(),
                        v.getUserName(),
                        v.getRole(),
                        v.getSocialLogin(),
                        v.getCoinBalance(),
                        v.getCreatedAt()
                ))
                .toList();
	}

}
