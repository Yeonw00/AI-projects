package com.example.newssummary.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
	private StringRedisTemplate redis;
	
	@Autowired
    public SocialAuthService(UserService userService, JwtTokenProvider jwtTokenProvider, 
    		RestTemplate restTemplate, StringRedisTemplate redis) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.restTemplate = restTemplate;
        this.redis = redis;
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
	@Value("${spring.security.oauth2.client.registration.naver.client-id}")
	private String naverClientId;
	
	@Value("${spring.security.oauth2.client.registration.naver.client-secret}")
	private String naverClientSecret;
	
	@Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
	private String naverRedirectUri;
	
	private final String NAVER_AUTH_URL = "https://nid.naver.com/oauth2.0/authorize";
	private final String NAVER_TOKEN_URL = "https://nid.naver.com/oauth2.0/token";
	private final String NAVER_USERINFO_URL = "https://openapi.naver.com/v1/nid/me";
	
	private final String KEY_PREFIX = "oauth:state:";
	private final Duration TTL = Duration.ofMinutes(10);	
	
	
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
	public String getNaverLoginUrl() {
		String state = issueAndStoreState();
		
		String url = UriComponentsBuilder
				.fromHttpUrl(NAVER_AUTH_URL)
				.queryParam("response_type", "code")
				.queryParam("client_id", naverClientId)
				.queryParam("redirect_uri", naverRedirectUri)
				.queryParam("state", state)
				.queryParam("auth_type", "reprompt")
				.build(true)
				.toUriString();
		return url;		
	}
	
	public String handleNaverCallback(String code, String state) {
		if (code == null || code.isBlank() || state == null || state.isBlank()) {
	        throw new IllegalArgumentException("Missing code/state");
	    }
		
		if(!verifyAndConsume(state)) {
			throw new SecurityException("Invalid state");
		}
		
		String accessToken = requestNaverAccessToken(code, state);
		
		Map<String, Object> userInfo = requestNaverUserInfo(accessToken);
		
		User user = userService.processNaverUser(userInfo);
		
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
		// User Info 요청
		if (accessToken == null || accessToken.isBlank()) {
			throw new IllegalArgumentException("accessToken is empty");
		}
		
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
				
		ResponseEntity<Map<String, Object>> resp = restTemplate.exchange(
				NAVER_USERINFO_URL, 
				HttpMethod.GET, 
				new HttpEntity<>(headers), 
				new ParameterizedTypeReference<Map<String, Object>>() {}
		);
		Map<String, Object> userInfo = resp.getBody();
		if (userInfo == null || userInfo.isEmpty()) {
			throw new IllegalStateException("Google userinfo is empty");
		}
		return userInfo;
	}
	
	// 공통 유틸
	private HttpHeaders formHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		return headers;
	}
	
	private String buildFrontendRedirect(String providerSuccessPath, String token, String username) {
		return String.format(
				"http://localhost:3000/%s?token=%s&username=%s", 
				providerSuccessPath,
				urlEnc(token),
				urlEnc(username)
		);
	}
	
	private String urlEnc(String v) {
		return URLEncoder.encode(v, StandardCharsets.UTF_8);
	}
	
	private String issueAndStoreState() {
		String state = UUID.randomUUID().toString();
		redis.opsForValue().set(KEY_PREFIX + state, "1", TTL);
		return state;
	}
	
	private boolean verifyAndConsume(String state) {
		String key = KEY_PREFIX + state;
		Boolean exists = redis.hasKey(key);
		if (Boolean.TRUE.equals(exists)) {
			redis.delete(key); // 1회용
			return true;
		}
		return false;
	}
}
