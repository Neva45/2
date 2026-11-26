package com.example.edu_plat.module_catalog.service;

import com.example.edu_plat.module_catalog.api.CatalogModuleApi;
import com.example.edu_plat.module_catalog.repository.CourseRepository;
import com.example.edu_plat.module_catalog.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;

@Service
public class CatalogModuleApiImpl implements CatalogModuleApi {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    public CatalogModuleApiImpl(EnrollmentRepository enrollmentRepository, CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public boolean isUserEnrolled(Long userId, Long courseId) {
        return enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .map(enrollment -> "ACTIVE".equals(enrollment.getStatus()))
                .orElse(false);
    }

    @Override
    public boolean courseExists(Long courseId) {
        return courseRepository.existsById(courseId);
    }

    @Override
    public boolean isCourseTeacher(Long teacherId, Long courseId) {
        return courseRepository.findById(courseId)
                .map(course -> teacherId.equals(course.getTeacherId()))
                .orElse(false);
    }

}
