package com.example.newssummary.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.newssummary.dao.SavedSummary;
import com.example.newssummary.dao.SummaryRequest;
import com.example.newssummary.dao.User;
import com.example.newssummary.dto.SavedSummaryDTO;
import com.example.newssummary.dto.SummaryRequestDTO;
import com.example.newssummary.repository.SavedSummaryRepository;
import com.example.newssummary.repository.SummaryRequestRepository;
import com.example.newssummary.security.CustomUserDetails;
import com.example.newssummary.service.SummaryService;
import com.example.newssummary.util.HtmlParser;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/summary")
public class SummaryController {
	
	@Autowired
	private SummaryService summaryService;
	
	@Autowired
	private SummaryRequestRepository summaryRequestRepository;
	
	@Autowired
	private SavedSummaryRepository savedSummaryRepository;
	
	@PostMapping("/huggingFace")
	public ResponseEntity<String> summarizeHuggingFace(@RequestBody SummaryRequestDTO request) {
		try {
			String content = summaryService.getArticleContent(request.getOriginalUrl());
			
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
	public ResponseEntity<String> summarizeOpenAi(@RequestBody SummaryRequestDTO requestDto, 
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		try {
			User user = userDetails.getUser();
			if(user == null) {
				return ResponseEntity.status(401).body("로그인이 필요합니다.");
			}
			
			String content = null;
			if(requestDto.getOriginalUrl() != null) {
				content = summaryService.getArticleContent(requestDto.getOriginalUrl());
			} else if(requestDto.getOriginalContent() != null) {
				content = requestDto.getOriginalContent();
			}
			if (content == null || content.trim().isEmpty()) {
			    return ResponseEntity.status(400).body("기사 본문을 추출하지 못했습니다. 다른 URL을 시도해주세요.");
			}
			String summary = summaryService.summarizeOpenAi(content);
			
			// 1. SummaryRequest 저장
			SummaryRequest summaryRequest = new SummaryRequest();
			summaryRequest.setUser(user);
			summaryRequest.setOriginalUrl(requestDto.getOriginalUrl());
			summaryRequest.setOriginalContent(content);
			summaryRequest.setSummaryResult(summary);
			summaryRequest.setCreatedAt(LocalDateTime.now());
			
			summaryRequestRepository.save(summaryRequest);
			
			// 2. SavedSummary 자동 저장
			SavedSummary saved = new SavedSummary();
			saved.setUser(user);
			saved.setSummaryRequest(summaryRequest);
			saved.setSavedAt(LocalDateTime.now());
			
			savedSummaryRepository.save(saved);
			
			return ResponseEntity.ok(summary);
		} catch (Exception e) {
		    e.printStackTrace();  // 콘솔에 전체 에러 로그 출력
		    return ResponseEntity.status(500).body("요약 실패: " + e.getMessage());
		}
	}
	
	@GetMapping("/list")
	public ResponseEntity<List<SavedSummaryDTO>> getSummaryList(
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		User user = userDetails.getUser();
		if (user == null) {
			return ResponseEntity.status(401).build();
		}
		List<SavedSummary> savedSummaries = savedSummaryRepository.findByUserId(user.getId());
		List<SavedSummaryDTO> dtoList = savedSummaries.stream()
				.map(SavedSummaryDTO::new)
				.toList();
		
		return ResponseEntity.ok(dtoList);
	}
	
	@GetMapping("/detail/{requestId}")
	public ResponseEntity<SavedSummaryDTO> getSavedSummary(@PathVariable Long requestId) {
		SavedSummary savedSummary = savedSummaryRepository.findById(requestId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		
		return ResponseEntity.ok(new SavedSummaryDTO(savedSummary));
	}
	
	@PatchMapping("/{id}/title")
	public ResponseEntity<Void> updateTitle(@PathVariable Long id, @RequestBody Map<String, String> body, 
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		User user = userDetails.getUser();
		if (user == null) return ResponseEntity.status(401).build();
		
		SavedSummary summary = savedSummaryRepository.findById(id).orElse(null);
		if(summary == null || !summary.getUser().getId().equals(user.getId())) {
			return ResponseEntity.status(403).build();
		}
		
		summary.setTitle(body.get("title"));
		savedSummaryRepository.save(summary);
		return ResponseEntity.ok().build();
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteSummary(@PathVariable Long id, 
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		User user = userDetails.getUser();
		if (user == null) return ResponseEntity.status(401).build();
		
		SavedSummary summary = savedSummaryRepository.findById(id).orElse(null);
		if(summary == null || !summary.getUser().getId().equals(user.getId())) {
			return ResponseEntity.status(403).build();
		}
		
		savedSummaryRepository.deleteById(id);
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/search")
	public ResponseEntity<?> searchSummary(@RequestParam String keyword, 
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		User user = userDetails.getUser();
		if (user == null) return ResponseEntity.status(401).build();
		
		Long userId = user.getId();
		
		List<SavedSummary> summaries = summaryService.searchByKeyword(userId, keyword);
		System.out.println("summaries : " + summaries);
		List<SavedSummaryDTO> dtoList = summaries.stream()
			    .map(SavedSummaryDTO::new)
			    .collect(Collectors.toList());

		return ResponseEntity.ok(dtoList);
	}
}
