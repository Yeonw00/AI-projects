package com.example.newssummary.dto;

public class SummaryRequest {
	
	private String url;
	
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
