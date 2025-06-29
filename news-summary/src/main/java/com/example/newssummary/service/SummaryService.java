package com.example.newssummary.service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.newssummary.config.HuggingFaceConfig;
import com.example.newssummary.config.OpenAiConfig;
import com.example.newssummary.dao.SummaryRequest;
import com.example.newssummary.dto.OpenAiResponse;
import com.example.newssummary.dto.SavedSummaryDTO;


@Service
public class SummaryService {
	
	private final HuggingFaceConfig huggingFaceConfig;
	private final OpenAiConfig openAiConfig;
	private RestTemplate restTemplate = new RestTemplate();
	
	@Autowired
	public SummaryService(HuggingFaceConfig huggingFaceConfig, RestTemplateBuilder builder, OpenAiConfig openAiConfig) {
		this.huggingFaceConfig = huggingFaceConfig;
		this.restTemplate = builder
		        .setConnectTimeout(Duration.ofSeconds(10))
		        .setReadTimeout(Duration.ofSeconds(120))
		        .build();
		this.openAiConfig = openAiConfig;
	}
	
	public String summarizeHuggingFace(String content) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", huggingFaceConfig.getApiToken());
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
		
		Map<String, String> body = Map.of("inputs", content);
		HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
		
		ResponseEntity<String> response = restTemplate.postForEntity(
				huggingFaceConfig.getApiUrl(), 
				request, 
				String.class);
		
		return response.getBody();
	}
	
	public String summarizeOpenAi(String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", openAiConfig.getApiKey());
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> body = Map.of(
            "model", "gpt-3.5-turbo",
            "messages", List.of(
                Map.of("role", "system", "content", 
                		"당신은 뉴스 기사를 요약하는 AI입니다. 사용자가 제공한 기사 내용을 기반으로만 요약하세요. 새로운 정보를 생성하지 마세요."),
                Map.of("role", "user", "content", "다음 뉴스 기사를 3~4문장으로 요약해 주세요:\n\n" + content)
            )
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<OpenAiResponse> response = restTemplate.postForEntity(
            "https://api.openai.com/v1/chat/completions",
            request,
            OpenAiResponse.class
        );

        return response.getBody()
                       .getChoices()
                       .get(0)
                       .getMessage()
                       .getContent()
                       .trim();
    }
	
}
