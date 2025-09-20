package com.example.newssummary.dto;

public class ConfirmResponse {
	private String status;
	private long grantedCoins;
	
	public ConfirmResponse() {}
	
	public ConfirmResponse(String status, long grantedCoins) {
		super();
		this.status = status;
		this.grantedCoins = grantedCoins;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getGrantedCoins() {
		return grantedCoins;
	}
	public void setGrantedCoins(long grantedCoins) {
		this.grantedCoins = grantedCoins;
	}
	
	
}
