package com.example.newssummary.dao;

import java.time.LocalDateTime;
import java.util.List;

import com.example.newssummary.dto.SummaryRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String username;
	
	private String email;
	
	@Column(name = "password_hash")
	private String passwordHash;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime lastLoginAt;
	
	@OneToMany(mappedBy = "user")
	private List<SummaryRequest> summaryRequests;
	
	@OneToMany(mappedBy = "user")
	private List<ErrorLog> errorLogs;
	
	@OneToMany(mappedBy = "user")
	private List<SavedSummary> savedSummaries;
}
