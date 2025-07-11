package com.example.newssummary.dao;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "saved_summaries")
public class SavedSummary {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "summary_request_id")
	private SummaryRequest summaryRequest;
	
	private LocalDateTime savedAt;
	
	private String title;

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

	public SummaryRequest getSummaryRequest() {
		return summaryRequest;
	}

	public void setSummaryRequest(SummaryRequest summaryRequest) {
		this.summaryRequest = summaryRequest;
	}

	public LocalDateTime getSavedAt() {
		return savedAt;
	}

	public void setSavedAt(LocalDateTime savedAt) {
		this.savedAt = savedAt;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "SavedSummary [id=" + id + ", user=" + user + ", summaryRequest=" + summaryRequest + ", savedAt="
				+ savedAt + ", title=" + title + "]";
	}
}
