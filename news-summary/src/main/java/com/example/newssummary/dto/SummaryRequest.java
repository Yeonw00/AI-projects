package com.example.newssummary.dto;

import jakarta.validation.constraints.NotBlank;

public class SummaryRequest {
	
	private String url;
	
	@NotBlank
	private String content;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "SummaryRequest [url=" + url + ", content=" + content + "]";
	}
	
}
