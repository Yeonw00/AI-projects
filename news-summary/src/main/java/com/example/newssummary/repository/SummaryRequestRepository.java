package com.example.newssummary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.newssummary.dao.SummaryRequest;

public interface SummaryRequestRepository extends JpaRepository<SummaryRequest, Long> {

}
