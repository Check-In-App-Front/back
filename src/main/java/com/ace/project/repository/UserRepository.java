package com.ace.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ace.project.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
