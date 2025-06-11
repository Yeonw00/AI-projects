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
}
