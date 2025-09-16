package com.example.newssummary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.newssummary.dao.CoinLedger;

public interface CoinLedgerRepository extends JpaRepository<CoinLedger, Long>{

}
