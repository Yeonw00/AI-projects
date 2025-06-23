package com.example.newssummary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.newssummary.dao.SavedSummary;

public interface SavedSummaryRepository extends JpaRepository<SavedSummary, Long>{

}
