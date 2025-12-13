package com.example.newssummary.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.newssummary.dao.CoinLedger;
import com.example.newssummary.dao.LedgerType;

public interface CoinLedgerRepository extends JpaRepository<CoinLedger, Long> {
    Page<CoinLedger> findByUserId(Long userId, Pageable pageable);
    Optional<CoinLedger> findByRequestId(String requestId); // 멱등 처리용
    List<CoinLedger> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, LedgerType type);
    
    @Query("""
    		select l from CoinLedger l
    		where l.user.id = :userId
	    		and (:type is null or l.type = :type)
	    		and (:from is null or l.createdAt >= :from)
	    		and (:to is null or l.createdAt <= :to)
	    	order by l.createdAt desc, l.id desc
    		
    """)
	Page<CoinLedger> findUserLedger(
			@Param("userId") Long userId, 
			@Param ("type") LedgerType type, 
			@Param("from") LocalDateTime from, 
			@Param("to") LocalDateTime to, 
			Pageable pageable
	);
}
