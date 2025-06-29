package com.example.newssummary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.newssummary.dao.SavedSummary;

public interface SavedSummaryRepository extends JpaRepository<SavedSummary, Long>{

	List<SavedSummary> findByUserId(Long id);

}
