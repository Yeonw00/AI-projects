package com.example.newssummary.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.newssummary.dto.AdminUserRow;
import com.example.newssummary.service.AdminUserService;

@RestController
@RequestMapping("/api/admin")
public class AdminUserController {

	@Autowired
	private AdminUserService adminUserService;
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/users")
	public List<AdminUserRow> getUsers() {
		return adminUserService.getAllUsers();
	}
}
