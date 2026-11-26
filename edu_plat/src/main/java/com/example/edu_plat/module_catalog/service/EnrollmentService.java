package com.example.edu_plat.module_catalog.service;

import com.example.edu_plat.exception.ResourceNotFoundException;
import com.example.edu_plat.module_catalog.dto.EnrollmentResponse;
import com.example.edu_plat.module_catalog.model.Enrollment;
import com.example.edu_plat.module_catalog.repository.CourseRepository;
import com.example.edu_plat.module_catalog.repository.EnrollmentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository, CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public EnrollmentResponse enrollUser(Long userId, Long courseId) {
        // Проверяем, существует ли курс
        courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        Optional<Enrollment> existingEnrollment = enrollmentRepository.findByUserIdAndCourseId(userId, courseId);
        Enrollment enrollment;

        if (existingEnrollment.isPresent()) {
            enrollment = existingEnrollment.get();
            enrollment.setStatus("ACTIVE"); // Если запись была отменена, делаем снова ACTIVE
            enrollment = enrollmentRepository.save(enrollment);
        } else {
            enrollment = new Enrollment();
            enrollment.setUserId(userId);
            enrollment.setCourseId(courseId);
            enrollment.setStatus("ACTIVE");
            enrollment = enrollmentRepository.save(enrollment);
        }

        return mapToResponse(enrollment);
    }

    @Transactional
    public EnrollmentResponse cancelEnrollment(Long userId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found for this user and course"));

        enrollment.setStatus("CANCELLED");
        return mapToResponse(enrollmentRepository.save(enrollment));
    }

    private EnrollmentResponse mapToResponse(Enrollment enrollment) {
        EnrollmentResponse response = new EnrollmentResponse();
        response.setId(enrollment.getId());
        response.setUserId(enrollment.getUserId());
        response.setCourseId(enrollment.getCourseId());
        response.setStatus(enrollment.getStatus());
        return response;
    }
}
