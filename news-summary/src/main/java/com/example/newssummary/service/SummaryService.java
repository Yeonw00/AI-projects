package com.example.newssummary.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.newssummary.config.HuggingFaceConfig;

@Service
public class SummaryService {
	
	private final HuggingFaceConfig huggingFaceConfig; 
	private final RestTemplate restTemplate = new RestTemplate();
	
	@Autowired
	public SummaryService(HuggingFaceConfig config) {
		this.huggingFaceConfig = config;
	}
	
	public String summarize(String content) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", huggingFaceConfig.getApiToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		Map<String, String> body = Map.of("inputs", content);
		HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
		
		ResponseEntity<String> response = restTemplate.postForEntity(
				huggingFaceConfig.getApiUrl(), 
				request, 
				String.class);
		
		return response.getBody();
	}
}
