package com.example.newssummary.dto;

import java.time.LocalDateTime;

public class SummaryPreviewDTO {
	private Long id;
	private String title;
	private LocalDateTime createdAt;
	
	public SummaryPreviewDTO() {}
	
	public SummaryPreviewDTO(Long id, String title, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.title = title;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@Override
	public String toString() {
		return "SummaryPreviewDTO [id=" + id + ", title=" + title + ", createdAt=" + createdAt + "]";
	}
}
