package com.example.newssummary.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "user_balance")
public class UserBalance {

	@Id
	private Long id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "user_id")
	private User user;
	
	@Column(nullable = false)
	private long balance;
	
	@Version
	private long version;
	
	
	public UserBalance() {}
	
	public UserBalance(Long id, User user, long balance, long version) {
		super();
		this.id = id;
		this.user = user;
		this.balance = balance;
		this.version = version;
	}
	
	public UserBalance(User user, long balance) {
		super();
		this.user = user;
		this.balance = balance;
	}


	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public long getBalance() {
		return balance;
	}
	
	public void setBalance(long balance) {
		this.balance = balance;
	}
	
	public long getVersion() {
		return version;
	}
	
	public void setVersion(long version) {
		this.version = version;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	

	public void increse(long amount) {
		this.balance += amount;
	}
	
	public void decrease(long amount) {
		this.balance -= amount;
	}

}
