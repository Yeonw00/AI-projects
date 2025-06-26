package com.example.newssummary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.newssummary.dao.SummaryRequest;

public interface SummaryRequestRepository extends JpaRepository<SummaryRequest, Long> {

	List<SummaryRequest> findByUserId(Long id);

}
