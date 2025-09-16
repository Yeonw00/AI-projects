package com.example.newssummary.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.newssummary.dao.PaymentOrder;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long>{
	Optional<PaymentOrder> findByOrderUid(String OrderUid);
}
