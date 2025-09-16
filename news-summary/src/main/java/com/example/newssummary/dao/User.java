package com.example.newssummary.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
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
	
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;
	
	@PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
	
	@Column(name = "last_login_at")
	private LocalDateTime lastLoginAt;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SummaryRequest> summaryRequests;
	
	@OneToMany(mappedBy = "user")
	private List<ErrorLog> errorLogs;
	
	@OneToMany(mappedBy = "user")
	private List<SavedSummary> savedSummaries;
	
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private UserBalance balance;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CoinLedger> ledgers;
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PaymentOrder> orders;
	
	public User() {
		
	}
	
	public User(Long id, String username, String email, String passwordHash, LocalDateTime createdAt,
			LocalDateTime lastLoginAt, List<SummaryRequest> summaryRequests, List<ErrorLog> errorLogs,
			List<SavedSummary> savedSummaries, UserBalance balance, List<CoinLedger> ledgers) {
		super();
		this.id = id;
		this.username = username;
		this.email = email;
		this.passwordHash = passwordHash;
		this.createdAt = createdAt;
		this.lastLoginAt = lastLoginAt;
		this.summaryRequests = summaryRequests;
		this.errorLogs = errorLogs;
		this.savedSummaries = savedSummaries;
		this.balance = balance;
		this.ledgers = ledgers;
	}

	// Google Oauth로 가입을 위한 생성자
	public User(String username, String email) {
	    this.username = username;
	    this.email = email;
	    this.createdAt = LocalDateTime.now();
	    this.lastLoginAt = LocalDateTime.now();
	    this.summaryRequests = new ArrayList<>();
	    this.errorLogs = new ArrayList<>();
	    this.savedSummaries = new ArrayList<>();
	    this.balance = balance;
	    this.ledgers = new ArrayList<>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getLastLoginAt() {
		return lastLoginAt;
	}

	public void setLastLoginAt(LocalDateTime lastLoginAt) {
		this.lastLoginAt = lastLoginAt;
	}

	public List<SummaryRequest> getSummaryRequests() {
		return summaryRequests;
	}

	public void setSummaryRequests(List<SummaryRequest> summaryRequests) {
		this.summaryRequests = summaryRequests;
	}

	public List<ErrorLog> getErrorLogs() {
		return errorLogs;
	}

	public void setErrorLogs(List<ErrorLog> errorLogs) {
		this.errorLogs = errorLogs;
	}

	public List<SavedSummary> getSavedSummaries() {
		return savedSummaries;
	}

	public void setSavedSummaries(List<SavedSummary> savedSummaries) {
		this.savedSummaries = savedSummaries;
	}
	
	public UserBalance getBalance() {
		return balance;
	}

	public void setBalance(UserBalance balance) {
		this.balance = balance;
	}

	public List<CoinLedger> getLedgers() {
		return ledgers;
	}

	public void setLedgers(List<CoinLedger> ledgers) {
		this.ledgers = ledgers;
	}
	
	public List<PaymentOrder> getOrders() {
		return orders;
	}

	public void setOrders(List<PaymentOrder> orders) {
		this.orders = orders;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", email=" + email + ", passwordHash=" + passwordHash
				+ ", createdAt=" + createdAt + ", lastLoginAt=" + lastLoginAt + "]";
	}
}
