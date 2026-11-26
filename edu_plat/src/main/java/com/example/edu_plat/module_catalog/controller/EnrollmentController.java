package com.example.edu_plat.module_catalog.controller;

import com.example.edu_plat.module_catalog.dto.EnrollmentRequest;
import com.example.edu_plat.module_catalog.dto.EnrollmentResponse;
import com.example.edu_plat.module_catalog.service.EnrollmentService;
import com.example.edu_plat.module_users.api.CustomUserDetails;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/enroll")
    public ResponseEntity<EnrollmentResponse> enroll(
            @RequestBody EnrollmentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        EnrollmentResponse response = enrollmentService.enrollUser(userDetails.getId(), request.getCourseId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel")
    public ResponseEntity<EnrollmentResponse> cancel(
            @RequestBody EnrollmentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        EnrollmentResponse response = enrollmentService.cancelEnrollment(userDetails.getId(), request.getCourseId());
        return ResponseEntity.ok(response);
    }
}
