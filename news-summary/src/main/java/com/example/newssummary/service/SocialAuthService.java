package com.example.newssummary.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.newssummary.dao.User;
import com.example.newssummary.security.JwtTokenProvider;

@Service
public class SocialAuthService {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private JwtTokenProvider jwtTokenProvider;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
    public SocialAuthService(UserService userService, JwtTokenProvider jwtTokenProvider, RestTemplate restTemplate) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.restTemplate = restTemplate;
    }
	
	// Google -------------------------------------------
	@Value("${spring.security.oauth2.client.registration.google.client-id}")
	private String clientId;
	
	@Value("${spring.security.oauth2.client.registration.google.client-secret}")
	private String clientSecret;
	
	@Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
	private String redirectUri;
	
	private final String TOKEN_URL = "https://oauth2.googleapis.com/token";
	private final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";
	
	
	public String handleGoogleCallback(String code) {
		String accessToken = requestGoogleAccessToken(code);
		Map<String, Object> userInfo = requestGoogleUserInfo(accessToken);
		 // DB 사용자 조회/회원가입 & JWT 발급
		User user = userService.processGoogleUser(userInfo);
		String username = user.getUsername();
		String token = jwtTokenProvider.generateToken(username);
	    
	    String redirectUrl = String.format(
	    		"http://localhost:3000/google-success?token=%s&username=%s",
	    		URLEncoder.encode(token, StandardCharsets.UTF_8),
	    		URLEncoder.encode(username, StandardCharsets.UTF_8)
	    );
	    
	    return redirectUrl;
	}

	public String getGoogleLoginUrl() {
		return "https://accounts.google.com/o/oauth2/v2/auth"
				+ "?client_id=" + clientId
				+ "&redirect_uri=" + redirectUri
				+ "&response_type=code"
				+ "&scope=profile email";
	}
	
	private String requestGoogleAccessToken(String code) {
		// Access Token 요청
		Map<String, String> tokenRequest = new HashMap<>();
		tokenRequest.put("code", code);
		tokenRequest.put("client_id", clientId);
		tokenRequest.put("client_secret", clientSecret);
		tokenRequest.put("redirect_uri", redirectUri);
		tokenRequest.put("grant_type", "authorization_code");
		
		Map<String, Object> tokenResponse = restTemplate.postForObject(TOKEN_URL, tokenRequest, Map.class);
		
		String accessToken = (String) tokenResponse.get("access_token");
		
		return accessToken;
	}
	
	private Map<String, Object> requestGoogleUserInfo(String accessToken) {
		// User Info 요청
		String userInfoEndpoint = USER_INFO_URL + "?access_token=" + accessToken;
		Map<String, Object> userInfo = restTemplate.getForObject(userInfoEndpoint, Map.class);
		
		return userInfo;
	}
}
