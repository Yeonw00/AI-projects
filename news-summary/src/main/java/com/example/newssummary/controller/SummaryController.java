package com.example.newssummary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.newssummary.dto.SummaryRequest;
import com.example.newssummary.service.SummaryService;
import com.example.newssummary.util.HtmlParser;

@RestController
@RequestMapping("/api/summary")
public class SummaryController {
	
	@Autowired
	private SummaryService summaryService;
	
	@PostMapping
	public ResponseEntity<String> summarize(@RequestBody SummaryRequest request) {
		try {
			String content = HtmlParser.extractArticle(request.getUrl());
			String summary = summaryService.summarize(content);
			return ResponseEntity.ok(summary);
		} catch (Exception e) {
			return ResponseEntity.status(500).body("요약 실패: " + e.getMessage());
		}
	}
}
