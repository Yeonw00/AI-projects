package com.example.newssummary.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.newssummary.dao.User;

public interface UserRepository extends JpaRepository<User, Long>{
	boolean existsByUsername(String username);
	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(String email);
}
