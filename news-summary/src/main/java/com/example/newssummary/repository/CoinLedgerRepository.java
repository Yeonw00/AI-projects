package com.example.newssummary.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.newssummary.dao.CoinLedger;
import com.example.newssummary.dao.LedgerType;

public interface CoinLedgerRepository extends JpaRepository<CoinLedger, Long> {
    Page<CoinLedger> findByUserIdAndType(Long userId, LedgerType type, Pageable pageable);
    Page<CoinLedger> findByUserId(Long userId, Pageable pageable);
    Optional<CoinLedger> findByRequestId(String requestId); // 멱등 처리용
}
