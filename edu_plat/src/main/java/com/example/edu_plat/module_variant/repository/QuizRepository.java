package com.example.edu_plat.module_variant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.edu_plat.module_variant.model.Quiz;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByCourseId(Long courseId);
}