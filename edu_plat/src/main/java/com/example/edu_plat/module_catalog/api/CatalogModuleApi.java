package com.example.edu_plat.module_catalog.api;

public interface CatalogModuleApi {
    boolean isUserEnrolled(Long userId, Long courseId);
    boolean courseExists(Long courseId);
    boolean isCourseTeacher(Long teacherId, Long courseId);
}
