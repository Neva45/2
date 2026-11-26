package com.example.edu_plat.module_catalog.repository;

import com.example.edu_plat.module_catalog.dto.CourseReportProjection;
import com.example.edu_plat.module_catalog.model.Course;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    // Пример пессимистической блокировки (PESSIMISTIC_WRITE) для предотвращения гонки потоков при обновлении курса
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Course c WHERE c.id = :id")
    Optional<Course> findByIdWithPessimisticLock(@Param("id") Long id);

     @Query(value = 
        "SELECT c.id as id, " +
        "       c.title as title, " +
        "       COUNT(DISTINCT CASE WHEN e.status = 'ACTIVE' THEN e.id END) as activeCount, " +
        "       COALESCE(CAST(SUM(CASE WHEN qa.passed = true THEN 1 ELSE 0 END) AS DOUBLE PRECISION) / NULLIF(COUNT(qa.id), 0), 0.0) as passRate " +
        "FROM courses c " +
        "LEFT JOIN enrollments e ON c.id = e.course_id " +
        "LEFT JOIN quizzes q ON c.id = q.course_id " +
        "LEFT JOIN quiz_attempts qa ON q.id = qa.quiz_id " +
        "GROUP BY c.id, c.title", 
        nativeQuery = true)
    List<CourseReportProjection> getCoursesReport();
}