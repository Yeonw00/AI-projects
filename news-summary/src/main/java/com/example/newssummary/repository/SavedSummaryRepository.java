package com.example.newssummary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.newssummary.dao.SavedSummary;

public interface SavedSummaryRepository extends JpaRepository<SavedSummary, Long>{

	List<SavedSummary> findByUserId(Long id);

	@Query("SELECT s FROM SavedSummary s JOIN FETCH s.summaryRequest sr " +
		       "WHERE s.user.id = :userId AND " +
		       "(s.title LIKE CONCAT('%', :keyword, '%') OR sr.summaryResult LIKE CONCAT('%', :keyword, '%'))")
	List<SavedSummary> searchByKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);

}
