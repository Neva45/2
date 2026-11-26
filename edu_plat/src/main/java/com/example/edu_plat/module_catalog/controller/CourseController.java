package com.example.edu_plat.module_catalog.controller;

import com.example.edu_plat.module_catalog.dto.CourseRequest;
import com.example.edu_plat.module_catalog.dto.CourseResponse;
import com.example.edu_plat.module_catalog.service.CourseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Курсы", description = "Управление учебными курсами")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @Operation(summary = "Получить список всех курсов (публичный метод)")
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @Operation(summary = "Получить курс по ID")
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @GetMapping("/report")
    public ResponseEntity<java.util.List<com.example.edu_plat.module_catalog.dto.CourseReportProjection>> getCoursesReport() {
        return ResponseEntity.ok(courseService.getCoursesReport());
    }

    @Operation(summary = "Создать новый курс", description = "Доступно только для TEACHER или ADMIN",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(
            @RequestBody @Valid CourseRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        CourseResponse created = courseService.createCourse(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/search/specification")
    public ResponseEntity<org.springframework.data.domain.Page<com.example.edu_plat.module_catalog.dto.CourseResponse>> searchBySpec(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        return ResponseEntity.ok(courseService.searchBySpecification(title, minPrice, maxPrice, page, size));
    }

    @GetMapping("/search/criteria")
    public ResponseEntity<org.springframework.data.domain.Page<com.example.edu_plat.module_catalog.dto.CourseResponse>> searchByCriteria(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        return ResponseEntity.ok(courseService.searchByCriteria(title, minPrice, maxPrice, page, size));
    }
}