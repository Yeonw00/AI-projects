package com.example.newssummary.dto;

import java.time.LocalDateTime;

import com.example.newssummary.dao.PaymentOrder;

public class RefundableOrderResponse {

    private Long id;
    private String orderUid;
    private String productName;
    private Long chargedCoins;
    private Long paidAmount;
    private LocalDateTime paidAt;

    public RefundableOrderResponse() {}

    public RefundableOrderResponse(
            Long id,
            String orderUid,
            String productName,
            Long chargedCoins,
            Long paidAmount,
            LocalDateTime paidAt
    ) {
        this.id = id;
        this.orderUid = orderUid;
        this.productName = productName;
        this.chargedCoins = chargedCoins;
        this.paidAmount = paidAmount;
        this.paidAt = paidAt;
    }

    public static RefundableOrderResponse from(PaymentOrder o) {
        return new RefundableOrderResponse(
                o.getId(),
                o.getOrderUid(),
                o.getProductCode(),
                o.getCoinAmount(),
                o.getPrice(),
                o.getPaidAt()
        );
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderUid() {
		return orderUid;
	}

	public void setOrderUid(String orderUid) {
		this.orderUid = orderUid;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Long getChargedCoins() {
		return chargedCoins;
	}

	public void setChargedCoins(Long chargedCoins) {
		this.chargedCoins = chargedCoins;
	}

	public Long getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(Long paidAmount) {
		this.paidAmount = paidAmount;
	}

	public LocalDateTime getPaidAt() {
		return paidAt;
	}

	public void setPaidAt(LocalDateTime paidAt) {
		this.paidAt = paidAt;
	}
}
