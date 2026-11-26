package com.example.edu_plat.module_variant.controller;

import com.example.edu_plat.module_variant.dto.QuizRequest;
import com.example.edu_plat.module_variant.dto.QuizResponse;
import com.example.edu_plat.module_variant.dto.QuizResultResponse;
import com.example.edu_plat.module_variant.dto.QuizSubmissionRequest;
import com.example.edu_plat.module_variant.service.QuizService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quizzes")
@Tag(name = "Тесты", description = "Управление тестами и их прохождение")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @Operation(summary = "Создать тест для курса", description = "Доступно только TEACHER или ADMIN",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<QuizResponse> createQuiz(
            @RequestBody @Valid QuizRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        QuizResponse created = quizService.createQuiz(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Получить тест по ID (без правильных ответов)",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{quizId}")
    public ResponseEntity<QuizResponse> getQuiz(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuizById(quizId));
    }

    @Operation(summary = "Пройти тест студентом", description = "Отправка ответов на проверку",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/{quizId}/submit")
    public ResponseEntity<QuizResultResponse> submitQuiz(
            @PathVariable Long quizId,
            @RequestBody @Valid QuizSubmissionRequest submission) {
        return ResponseEntity.ok(quizService.submitQuiz(quizId, submission));
    }
}