package com.example.newssummary.dto;

public class SummaryRequestDTO {
	
	private String originalUrl;
	private String originalContent;
	
	public String getOriginalUrl() {
		return originalUrl;
	}
	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}
	public String getOriginalContent() {
		return originalContent;
	}
	public void setOriginalContent(String originalContent) {
		this.originalContent = originalContent;
	}
	
	@Override
	public String toString() {
		return "SummaryRequestDTO [originalUrl=" + originalUrl + ", originalContent=" + originalContent + "]";
	}
}
