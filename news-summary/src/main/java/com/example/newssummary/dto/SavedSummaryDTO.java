package com.example.newssummary.dto;

import java.time.LocalDateTime;

import com.example.newssummary.dao.SavedSummary;

public class SavedSummaryDTO {
	private Long id;
	private String url;
	private String originalContent;
	private String summary;
	private LocalDateTime createdAt;
	private LocalDateTime savedAt;
	private String username;
	private String title;
	
	public SavedSummaryDTO() {}
	
	public SavedSummaryDTO(Long id, LocalDateTime createdAt, LocalDateTime savedAt, String title) {
		this.id = id;
		this.createdAt = createdAt;
		this.savedAt = savedAt;
		this.title = title;
	}
	
	public SavedSummaryDTO(SavedSummary savedSummary) {
		this.id = savedSummary.getId();
		this.url = savedSummary.getSummaryRequest().getOriginalUrl();
		this.originalContent = savedSummary.getSummaryRequest().getOriginalContent();
		this.summary = savedSummary.getSummaryRequest().getSummaryResult();
		this.createdAt = savedSummary.getSummaryRequest().getCreatedAt();
		this.savedAt = savedSummary.getSavedAt();
		this.username = savedSummary.getUser().getUsername();
		this.title = savedSummary.getTitle();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getOriginalContent() {
		return originalContent;
	}

	public void setOriginalContent(String originalContent) {
		this.originalContent = originalContent;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getSavedAt() {
		return savedAt;
	}

	public void setSavedAt(LocalDateTime savedAt) {
		this.savedAt = savedAt;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "SavedSummaryDTO [id=" + id + ", url=" + url + ", originalContent=" + originalContent + ", summary="
				+ summary + ", createdAt=" + createdAt + ", savedAt=" + savedAt + ", username=" + username + ", title="
				+ title + "]";
	}
}
