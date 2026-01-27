package com.example.newssummary.dto;

import java.util.List;

public class LedgerPageResponse {
	private List<LedgerEntryResponse> items;
    private long totalCount;
    private int page;
    private int size;
    private long currentBalance;   // 현재 잔액(상단 표시용)
    
    public LedgerPageResponse() {}

	public LedgerPageResponse(List<LedgerEntryResponse> items, long totalCount, int page, int size,
			long currentBalance) {
		super();
		this.items = items;
		this.totalCount = totalCount;
		this.page = page;
		this.size = size;
		this.currentBalance = currentBalance;
	}

	public List<LedgerEntryResponse> getItems() {
		return items;
	}

	public void setItems(List<LedgerEntryResponse> items) {
		this.items = items;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public long getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(long currentBalance) {
		this.currentBalance = currentBalance;
	}
}
