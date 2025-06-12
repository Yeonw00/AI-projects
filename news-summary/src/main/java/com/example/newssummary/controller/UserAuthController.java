package com.example.newssummary.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.newssummary.dao.User;
import com.example.newssummary.dto.UserLoginRequest;
import com.example.newssummary.dto.UserRegisterRequest;
import com.example.newssummary.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
public class UserAuthController {
	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody UserRegisterRequest request) {
		if (userRepository.existsByUsername(request.getUsername())) {
			return ResponseEntity.badRequest().body("이미 존재하는 사용자입니다.");
		}
		
		User user = new User();
		user.setUsername(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
		user.setCreatedAt(LocalDateTime.now());
		
		userRepository.save(user);
		return ResponseEntity.ok("회원가입 성공");
	}
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody UserLoginRequest request) {
		Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
		if(userOpt.isPresent()) {
			User user = userOpt.get();
			if(passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
				user.setLastLoginAt(LocalDateTime.now());
				userRepository.save(user);
				return ResponseEntity.ok("로그인 성공");
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("자격 오류");
	}
}
