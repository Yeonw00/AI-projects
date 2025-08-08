package com.example.newssummary.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.newssummary.config.HuggingFaceConfig;
import com.example.newssummary.config.OpenAiConfig;
import com.example.newssummary.dao.SavedSummary;
import com.example.newssummary.dao.SummaryRequest;
import com.example.newssummary.dao.User;
import com.example.newssummary.dto.OpenAiResponse;
import com.example.newssummary.repository.SavedSummaryRepository;
import com.example.newssummary.util.HtmlParser;
import com.example.newssummary.util.WebDriverFactory;


@Service
public class SummaryService {
	
	private final HuggingFaceConfig huggingFaceConfig;
	private final OpenAiConfig openAiConfig;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private SavedSummaryRepository savedSummaryRepository;
	
	@Autowired
	private TitleExtractor titleExtractor;
	
	@Autowired
	public SummaryService(HuggingFaceConfig huggingFaceConfig, RestTemplate restTemplate, OpenAiConfig openAiConfig, SavedSummaryRepository savedSummaryRepository, TitleExtractor titleExtractor) {
		this.huggingFaceConfig = huggingFaceConfig;
		this.restTemplate = restTemplate;
		this.openAiConfig = openAiConfig;
		this.savedSummaryRepository = savedSummaryRepository;
		this.titleExtractor = titleExtractor;
	}
	
	public String getArticleContent(String url) {
		WebDriver driver = null;
		try {
			driver = WebDriverFactory.createDriver();
			return HtmlParser.extractArticle(driver, url);
		} catch (Exception e) {
			System.err.println("SummaryService - 기사 추출 중 오류: "+ e.getMessage());
			return "";
		} finally {
			if (driver != null) {
				driver.quit();
			}
		}
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
	
	public List<SavedSummary> searchByKeyword(Long userId, String keyword) {
		return savedSummaryRepository.searchByKeyword(userId, keyword);
	}
	
	public void createSavedSummary(User user, SummaryRequest summaryRequest) {
		SavedSummary saved = new SavedSummary();
		saved.setUser(user);
		saved.setSummaryRequest(summaryRequest);
		saved.setSavedAt(LocalDateTime.now());
		
		String title = titleExtractor.extractTitle(summaryRequest.getOriginalUrl());
		if (title == null) title = deriveFromContent(summaryRequest.getOriginalContent(), summaryRequest.getSummaryResult());
		if (title == null || title.isBlank()) title = "제목 없음";
		
		saved.setTitle(title);
		
		savedSummaryRepository.save(saved);
	}
	
	private String deriveFromContent(String originalContent, String summaryResult) {
		String candidate = firstMeaningfulLine(summaryResult);
		if (candidate == null) candidate = firstMeaningfulLine(originalContent);
		if (candidate == null) return null;
		
		candidate = candidate.replaceAll("\\s+", " ").trim();
		return candidate.length() > 80 ? candidate.substring(0, 80) + "..." : candidate;
	}
	
	private String firstMeaningfulLine(String text) {
		if (text == null) return null;
		String[] parts = text.split("[\\n\\r\\.?!]");
		for (String p : parts) {
			String s = p.replaceAll("\\s+", " ").trim();
			if (s.length() >= 5) return s;
		}
		return null;
	}
}
