package com.example.newssummary.dao;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
	name = "summary_requests",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = "request_id")
	}
)
public class SummaryRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "request_id", nullable = false, updatable = false)
	private String requestId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	
	private String originalUrl;
	
	@Column(columnDefinition = "TEXT")
	private String originalContent;
	
	@Column(columnDefinition = "TEXT")
	private String summaryResult;
	
	private String sourceSite;
	
	private LocalDateTime createdAt;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SummaryStatus status;
	
	@OneToMany(mappedBy = "summaryRequest")
	private List<SavedSummary> savedSummaries;
	
	public SummaryRequest() {}
	
	public SummaryRequest(Long id, String requestId, User user, String originalUrl, String originalContent,
			String summaryResult, String sourceSite, LocalDateTime createdAt, SummaryStatus status,
			List<SavedSummary> savedSummaries) {
		super();
		this.id = id;
		this.requestId = requestId;
		this.user = user;
		this.originalUrl = originalUrl;
		this.originalContent = originalContent;
		this.summaryResult = summaryResult;
		this.sourceSite = sourceSite;
		this.createdAt = createdAt;
		this.status = status;
		this.savedSummaries = savedSummaries;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

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

	public String getSummaryResult() {
		return summaryResult;
	}

	public void setSummaryResult(String summaryResult) {
		this.summaryResult = summaryResult;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<SavedSummary> getSavedSummaries() {
		return savedSummaries;
	}

	public void setSavedSummaries(List<SavedSummary> savedSummaries) {
		this.savedSummaries = savedSummaries;
	}
	
	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public SummaryStatus getStatus() {
		return status;
	}

	public void setStatus(SummaryStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "SummaryRequest [id=" + id + ", userId=" + (user != null ? user.getId() : null) + ", originalUrl=" + originalUrl + ", sourceSite="
				+ sourceSite + ", createdAt=" + createdAt + "]";
	}
}
