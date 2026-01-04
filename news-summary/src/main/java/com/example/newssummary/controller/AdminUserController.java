package com.example.newssummary.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.newssummary.dto.admin.AdminUserView;
import com.example.newssummary.service.AdminUserService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

	@Autowired
	private AdminUserService adminUserService;
	
	
	@GetMapping("/users")
	public Page<AdminUserView> getUsers(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "20") int size,
		@RequestParam(required = false) String keyword,
		@RequestParam(required = false) String role,
		@RequestParam(required = false) Boolean social
	) {
		return adminUserService.getUsers(page, size, keyword, role, social);
	}
	
	@GetMapping("/users/export")
	public void exportUsers(
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String role,
			@RequestParam(required = false) Boolean social,
			HttpServletResponse response
	) throws IOException {
			List<AdminUserView> rows = adminUserService.getUsersForExport(keyword, role, social);
			
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-Disposition", "attachment; filename=\"user-liset.xlsx\"");
			adminUserService.exportUsers(rows, response.getOutputStream());
	}
}
