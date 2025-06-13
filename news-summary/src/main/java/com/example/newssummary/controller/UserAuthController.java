package com.example.newssummary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.newssummary.dao.User;
import com.example.newssummary.dto.SignupRequest;
import com.example.newssummary.dto.UserLoginRequest;
import com.example.newssummary.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class UserAuthController {
	@Autowired
	private UserService userService;
	
	@PostMapping("/signup")
	public ResponseEntity<?> signup(@RequestBody SignupRequest request){
		userService.singup(request);
		return ResponseEntity.ok("회원가입 성공");
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody UserLoginRequest request, HttpSession session) {
		User user = userService.login(request);
		session.setAttribute("user", user);
		return ResponseEntity.ok("로그인 성공");
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpSession session) {
		session.invalidate();
		return ResponseEntity.ok("로그아웃 성공");
	}
}
