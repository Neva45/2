package com.example.edu_plat.module_catalog.dto;

public interface CourseReportProjection {
    Long getId();
    String getTitle();
    Long getActiveCount();
    Double getPassRate(); // Доля успешных попыток теста курса (от 0.0 до 1.0)
}
