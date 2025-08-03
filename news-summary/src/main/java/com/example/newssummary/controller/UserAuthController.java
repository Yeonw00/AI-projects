package com.example.newssummary.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.newssummary.dao.User;
import com.example.newssummary.dto.SignupRequest;
import com.example.newssummary.dto.UserLoginRequest;
import com.example.newssummary.repository.UserRepository;
import com.example.newssummary.security.CustomUserDetails;
import com.example.newssummary.security.JwtTokenProvider;
import com.example.newssummary.service.UserService;

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
	
	@Value("${spring.security.oauth2.client.registration.google.client-id}")
	private String clientId;
	
	@Value("${spring.security.oauth2.client.registration.google.client-secret}")
	private String clientSecret;
	
	@Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
	private String redirectUri;
	
	private final String TOKEN_URL = "https://oauth2.googleapis.com/token";
	private final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";
	
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
			Map<String, String> error = new HashMap<>();
			error.put("message", "아이디 또는 비밀번호가 잘못되었습니다.");
			
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
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
	public ResponseEntity<?> logout() {
		return ResponseEntity.ok("로그아웃 성공");
	}
	
	@GetMapping("/check")
	public ResponseEntity<Map<String, Object>> checkLogin(
			@AuthenticationPrincipal CustomUserDetails userDetails) {
	    Object user = userDetails.getUser();
	    Map<String, Object> result = new HashMap<>();
	    result.put("loggedIn", user != null);
	    return ResponseEntity.ok(result);
	}
	
	@PatchMapping("/me")
	public ResponseEntity<?> updateUser(@RequestBody Map<String, String> request, 
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		String email = request.get("email");
		String newPassword = request.get("newPassword");
		String currentPassword = request.get("currentPassword");
		
		User user = userDetails.getUser();
		if(user == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		
		
		if(email !=null && !email.isBlank() && !email.matches("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")) {
			user.setEmail(email);
		}
		
		if(newPassword != null && !newPassword.isBlank()) {
			if(!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("현재 비밀번호가 올바르지 않습니다.");
			}
			user.setPasswordHash(passwordEncoder.encode(newPassword));
		}
		
		userRepository.save(user);
		return ResponseEntity.ok("회원 정보가 성공적으로 수정되었습니다.");
	}
	
	@GetMapping("/google/login")
	public String googleLoginUrl() {
		return "https://accounts.google.com/o/oauth2/v2/auth"
				+ "?client_id=" + clientId
				+ "&redirect_uri=" + redirectUri
				+ "&response_type=code"
				+ "&scope=profile email";
	}
	
//	google 로그인 callback (code -> Access Token -> User Info)
	@GetMapping("/google/callback")
	public ResponseEntity<?> googleCallback(@RequestParam("code") String code) {
		RestTemplate restTemplate = new RestTemplate();
		
		// Access Token 요청
		Map<String, String> tokenRequest = new HashMap<>();
		tokenRequest.put("code", code);
		tokenRequest.put("client_id", clientId);
		tokenRequest.put("client_secret", clientSecret);
		tokenRequest.put("redirect_uri", redirectUri);
		tokenRequest.put("grant_type", "authorization_code");
		
		Map<String, Object> tokenResponse = restTemplate.postForObject(TOKEN_URL, tokenRequest, Map.class);
		
		String accessToken = (String) tokenResponse.get("access_token");
		
		// User Info 요청
		String userInfoEndpoint = USER_INFO_URL + "?access_token=" + accessToken;
		Map<String, Object> userInfo = restTemplate.getForObject(userInfoEndpoint, Map.class);
		
		// DB 사용자 조회/회원가입 & JWT 발급
		String token = userService.processGoogleUser(userInfo);
		
		Map<String, Object> response = new HashMap<>();
	    response.put("token", token);
	    response.put("user", userInfo);
	    
	    return ResponseEntity.ok(response);
	}
}
