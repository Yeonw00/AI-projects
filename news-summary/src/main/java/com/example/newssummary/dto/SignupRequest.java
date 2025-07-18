package com.example.newssummary.dto;

import jakarta.validation.constraints.Size;

public class SignupRequest {
	private String username;
	private String email;
	
	@Size(min = 6, message = "비밀번호는 6자 이상이어야 합니다.")
	private String password;
	
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String toString() {
		return "SignupRequest [username=" + username + ", email=" + email + ", password=" + password + "]";
	}
}
