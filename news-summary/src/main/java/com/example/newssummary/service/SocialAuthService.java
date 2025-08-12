package com.example.newssummary.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
	private String googleClientId;
	
	@Value("${spring.security.oauth2.client.registration.google.client-secret}")
	private String googleClientSecret;
	
	@Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
	private String googleRedirectUri;
	
	private final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
	private final String GOOGLE_USERINFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";
	
	// Naver -------------------------------------------
	@Value("{spring.security.oauth2.client.registration.naver.client-id}")
	private String naverClientId;
	
	@Value("{spring.security.oauth2.client.registration.naver.client-secret}")
	private String naverClientSecret;
	
	@Value("{spring.security.oauth2.client.registration.naver.redirect-uri}")
	private String naverRedirectUri;
	
	private final String NAVER_AUTH_URL = "https://nid.naver.com/oauth2.0/authorize";
	private final String NAVER_TOKEN_URL = "https://nid.naver.com/oauth2.0/token";
	private final String NAVER_USERINFO_URL = "https://openapi.naver.com/v1/nid/me";
	
	
	
	public String getGoogleLoginUrl() {
		return "https://accounts.google.com/o/oauth2/v2/auth"
				+ "?client_id=" + googleClientId
				+ "&redirect_uri=" + googleRedirectUri
				+ "&response_type=code"
				+ "&scope=profile email";
	}
	
	public String handleGoogleCallback(String code) {
		String accessToken = requestGoogleAccessToken(code);
		Map<String, Object> userInfo = requestGoogleUserInfo(accessToken);
		 // DB 사용자 조회/회원가입 & JWT 발급
		User user = userService.processGoogleUser(userInfo);
		String username = user.getUsername();
		String token = jwtTokenProvider.generateToken(username);
	    
		return buildFrontendRedirect("google-success", token, username);
	}

	private String requestGoogleAccessToken(String code) {
		// Access Token 요청
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("code", code);
		body.add("client_id", googleClientId);
		body.add("client_secret", googleClientSecret);
		body.add("redirect_uri", googleRedirectUri);
		body.add("grant_type", "authorization_code");
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
		
		ResponseEntity<Map<String, Object>> resp = restTemplate.exchange(
				GOOGLE_TOKEN_URL, 
				HttpMethod.POST,
				request, 
				new ParameterizedTypeReference<>() {}
		);
		
		Map<String, Object> tokenResponse = resp.getBody();
		if (tokenResponse == null || tokenResponse.get("access_token") == null) {
			throw new IllegalStateException("Google token request failed : " + tokenResponse);
		}
		return (String) tokenResponse.get("access_token");
	}
	
	private Map<String, Object> requestGoogleUserInfo(String accessToken) {
		// User Info 요청
		if (accessToken == null || accessToken.isBlank()) {
			throw new IllegalArgumentException("accessToken is empty");
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		
		ResponseEntity<Map<String, Object>> resp = restTemplate.exchange(
				GOOGLE_USERINFO_URL,
				HttpMethod.GET, 
				new HttpEntity<>(headers), 
				new ParameterizedTypeReference<>() {}
		);
		
		Map<String, Object> userInfo = resp.getBody();
		if (userInfo == null || userInfo.isEmpty()) {
			throw new IllegalStateException("Google userinfo is empty");
		}
		return userInfo;
	}
	
	// state는 CSRF 방지를 위해 서버에서 생성/검증해야 함
	public String getNaverLoginUrl(String state) {
		return NAVER_AUTH_URL
				+ "?response_type=code"
				+ "&client_id=" + naverClientId
				+ "&redirect_uri=" + urlEnc(naverRedirectUri)
				+ "&state=" + urlEnc(state);
	}
	
	public String handleNaverCallback(String code, String state) {
		String accessToken = requestNaverAccessToken(code, state);
		Map<String, Object> userInfo = requestNaverUserInfo(accessToken);
		
		Map<String, Object> response = (Map<String, Object>) userInfo.get("response");
		User user = userService.processNaverUser(response);
		
		String username = user.getUsername();
		String token = jwtTokenProvider.generateToken(username);
		return buildFrontendRedirect("naver-success", token, username);
	}
	
	private String requestNaverAccessToken(String code, String state) {
		HttpHeaders headers = formHeaders();
		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("grant_type", "authorization_code");
		body.add("client_id", naverClientId);
		body.add("client_secret", naverClientSecret);
		body.add("code", code);
		body.add("state", state);
		
		HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(body, headers);
		ResponseEntity<Map> resp = restTemplate.postForEntity(NAVER_TOKEN_URL, req, Map.class);
		return (String) resp.getBody().get("access_token");
	}
	
	private Map<String, Object> requestNaverUserInfo(String accessToken) {
		HttpHeaders headers = bearerHeaders(accessToken);
		ResponseEntity<Map<String, Object>> resp = restTemplate.exchange(
				NAVER_USERINFO_URL, 
				HttpMethod.GET, 
				new HttpEntity<>(headers), 
				new ParameterizedTypeReference<Map<String, Object>>() {}
		);
		return resp.getBody();
	}
	
	// 공통 유틸
	private HttpHeaders formHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		return headers;
	}
	
	private HttpHeaders bearerHeaders(String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(accessToken);
		return headers;
	}
	
	private String buildFrontendRedirect(String providerSuccessPath, String token, String username) {
		return String.format(
				"http://localhost:3000/%s?token=%s&username=%S", 
				providerSuccessPath,
				urlEnc(token),
				urlEnc(username)
		);
	}
	
	private String urlEnc(String v) {
		return URLEncoder.encode(v, StandardCharsets.UTF_8);
	}
}
