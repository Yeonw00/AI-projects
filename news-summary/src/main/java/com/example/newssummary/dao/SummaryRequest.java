package com.example.newssummary.dao;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "summary_requests")
public class SummaryRequest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	private String originalUrl;
	
	@Column(columnDefinition = "TEXT")
	private String originalContent;
	
	@Column(columnDefinition = "TEXT")
	private String summaryResult;
	
	private String sourceSite;
	
	private LocalDateTime createdAt;
	
	@OneToMany(mappedBy = "summaryRequest")
	private List<SavedSummary> savedSummaries;
}
