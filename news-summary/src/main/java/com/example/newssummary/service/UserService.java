package com.example.newssummary.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.newssummary.dao.User;
import com.example.newssummary.dto.SignupRequest;
import com.example.newssummary.dto.UserLoginRequest;
import com.example.newssummary.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public void singup(SignupRequest request) {
		if (userRepository.findByUsername(request.getUsername()).isPresent()) {
			throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
		}
		User user = new User();
		user.setUsername(request.getUsername());
		user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
		user.setEmail(request.getEmail());
		userRepository.save(user);
	}
	
	public User login(UserLoginRequest request) {
		User user = userRepository.findByUsername(request.getUsername())
					.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
		
		if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
			throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
		}
		return user;
	}
	
	@Transactional
	public User processGoogleUser(Map<String, Object> userInfo) {
		// 1.Google에서 받은 정보 추출
		String email = (String) userInfo.get("email");
//		String googleName = (String) userInfo.get("name");
//		String googleId = (String) userInfo.get("id");
//		String picture = (String) userInfo.get("picture");
		
		// 2.사용자 존재 여부 확인
		Optional<User> optionalUser = userRepository.findByEmail(email);
		User user;
		
		if(optionalUser.isPresent()) {
			// 기존 사용자
			user = optionalUser.get();
			user.setLastLoginAt(LocalDateTime.now());
			userRepository.save(user);
		} else {
			// 신규 가입 
			String baseUsername = email.split("@")[0];
			String username = generateUniqueUsername(baseUsername);
			
			user = new User(username, email);
			userRepository.save(user);
		}
		
		// 3.JWT 토큰 생성
		return user;
	}
	
	private String generateUniqueUsername(String baseUsername) {
		String username = baseUsername;
		int count = 1;
		
		while (userRepository.existsByUsername(username)) {
			username = baseUsername + count;
			count ++;
		}
		return username;
	}

	@Transactional
	public User processNaverUser(Map<String, Object> naver) {
		// 1.Naver에서 받은 정보 추출
		String naverId = (String) naver.get("id");
		String email = (String) naver.get("email");
		
		if (naverId == null || naverId.isBlank()) {
			throw new IllegalArgumentException("naver id is missing");
		}
		
		// 2.사용자 존재 여부 확인
		Optional<User> optionalUser = userRepository.findByEmail(email);
		User user;
		
		if(optionalUser.isPresent()) {
			// 기존 사용자
			user = optionalUser.get();
			user.setLastLoginAt(LocalDateTime.now());
			userRepository.save(user);
		} else {
			// 신규 가입 
			String baseUsername = naverId;
			String username = generateUniqueUsername(baseUsername);
			
			user = new User(username, email);
			userRepository.save(user);
		}
		
		// 3.JWT 토큰 생성
		return user;
	}
}
