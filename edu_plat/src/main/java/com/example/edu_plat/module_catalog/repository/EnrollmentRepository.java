package com.example.edu_plat.module_catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.edu_plat.module_catalog.model.Enrollment;

import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);
}
