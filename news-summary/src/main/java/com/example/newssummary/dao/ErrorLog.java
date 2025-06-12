package com.example.newssummary.dao;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "error_logs")
public class ErrorLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(optional = true)
	@JoinColumn(name = "user_id", nullable = true)
	private User user;
	
	@Column(columnDefinition = "TEXT")
	private String errorMessage;
	
	@Column(columnDefinition = "TEXT")
	private String stackTrace;
	
	@Column(columnDefinition = "TEXT")
	private String requestData;
	
	private LocalDateTime loggedAt;

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

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public String getRequestData() {
		return requestData;
	}

	public void setRequestData(String requestData) {
		this.requestData = requestData;
	}

	public LocalDateTime getLoggedAt() {
		return loggedAt;
	}

	public void setLoggedAt(LocalDateTime loggedAt) {
		this.loggedAt = loggedAt;
	}

	@Override
	public String toString() {
		return "ErrorLog [id=" + id + ", user=" + user + ", errorMessage=" + errorMessage + ", stackTrace=" + stackTrace
				+ ", requestData=" + requestData + ", loggedAt=" + loggedAt + "]";
	}
}
