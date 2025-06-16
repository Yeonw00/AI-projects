package com.example.newssummary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.newssummary.dao.SummaryRequest;
import com.example.newssummary.service.SummaryService;
import com.example.newssummary.util.HtmlParser;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/summary")
public class SummaryController {
	
	@Autowired
	private SummaryService summaryService;
	
	@PostMapping("/huggingFace")
	public ResponseEntity<String> summarizeHuggingFace(@RequestBody SummaryRequest request) {
		try {
			String content = HtmlParser.extractArticle(request.getOriginalUrl());
			
			if (content.length() > 1000) {
			    content = content.substring(0, 1000);
			}
			
			System.out.println("요약 대상 글자 수: " + content.length());
			
			String summary = summaryService.summarizeHuggingFace(content);
			System.out.println("요약 결과:\n" + summary);
			return ResponseEntity.ok(summary);
		} catch (Exception e) {
		    e.printStackTrace();  // 콘솔에 전체 에러 로그 출력
		    return ResponseEntity.status(500).body("요약 실패: " + e.getMessage());
		}
	}
	
	@PostMapping("/openai")
	public ResponseEntity<String> summarizeOpenAi(@RequestBody SummaryRequest request) {
		try {
			String content = null;
			if(request.getOriginalUrl() != null) {
				content = HtmlParser.extractArticle(request.getOriginalUrl());
			} else if(request.getOriginalContent() != null) {
				content = request.getOriginalContent();
			}
			if (content == null || content.trim().isEmpty()) {
			    return ResponseEntity.status(400).body("기사 본문을 추출하지 못했습니다. 다른 URL을 시도해주세요.");
			}
			String summary = summaryService.summarizeOpenAi(content);
			System.out.println("요약 결과:\n" + summary);
			return ResponseEntity.ok(summary);
		} catch (Exception e) {
		    e.printStackTrace();  // 콘솔에 전체 에러 로그 출력
		    return ResponseEntity.status(500).body("요약 실패: " + e.getMessage());
		}
	}
}
