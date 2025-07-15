package com.example.newssummary.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.newssummary.dao.User;
import com.example.newssummary.dto.SignupRequest;
import com.example.newssummary.dto.UserLoginRequest;
import com.example.newssummary.repository.UserRepository;
import com.example.newssummary.security.JwtTokenProvider;
import com.example.newssummary.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class UserAuthController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@PostMapping("/signup")
	public ResponseEntity<?> signup(@RequestBody SignupRequest request){
		userService.singup(request);
		return ResponseEntity.ok("회원가입 성공");
	}
	
	// 세션 사용 시 login로직
//	@PostMapping("/login")
//	public ResponseEntity<?> login(@RequestBody UserLoginRequest request, HttpSession session) {
//		User user = userService.login(request);
//		session.setAttribute("user", user);
//		// 비밀번호 검증 등 처리 후 로그인 성공 시:
//	    user.setLastLoginAt(LocalDateTime.now());
//	    userRepository.save(user);
//		return ResponseEntity.ok(user);
//	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody UserLoginRequest request) {
		User user = userService.login(request);
		
		if(user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
		}
		
		String token = jwtTokenProvider.generateToken(user.getUsername());
		
		user.setLastLoginAt(LocalDateTime.now());
		userRepository.save(user);
		
		Map<String, Object> response = new HashMap<>();
		response.put("token", token);
		response.put("username", user.getUsername());
		response.put("email", user.getEmail());
		
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpSession session) {
		session.invalidate();
		return ResponseEntity.ok("로그아웃 성공");
	}
	
	@GetMapping("/check")
	public ResponseEntity<Map<String, Object>> checkLogin(HttpSession session) {
	    Object user = session.getAttribute("user");
	    Map<String, Object> result = new HashMap<>();
	    result.put("loggedIn", user != null);
	    return ResponseEntity.ok(result);
	}
	
	@PatchMapping("/update")
	public ResponseEntity<?> updateUser(@RequestBody Map<String, String> request, HttpSession session) {
		String email = request.get("email");
		String newPassword = request.get("newPassword");
		String currentPassword = request.get("currentPassword");
		
		User user = (User) session.getAttribute("user");
		if(user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		if(!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("현재 비밀번호가 올바르지 않습니다.");
		}
		
		if(email !=null && !email.isBlank()) {
			user.setEmail(email);
		}
		
		if(newPassword != null && !newPassword.isBlank()) {
			user.setPasswordHash(passwordEncoder.encode(newPassword));
		}
		
		userRepository.save(user);
		return ResponseEntity.ok().build();
	}
}
