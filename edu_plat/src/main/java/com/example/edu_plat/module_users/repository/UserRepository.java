package com.example.edu_plat.module_users.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.edu_plat.module_users.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}