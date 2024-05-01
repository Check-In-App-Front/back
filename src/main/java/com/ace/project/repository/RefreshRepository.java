package com.ace.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ace.project.entity.Refresh;

import jakarta.transaction.Transactional;

@Repository
public interface RefreshRepository extends JpaRepository<Refresh, Long> {
	Boolean existsByRefresh(String refresh);
	
	@Transactional
    void deleteByRefresh(String refresh);
}
