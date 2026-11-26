package com.example.edu_plat.module_variant.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class QuizRequest {

    @NotBlank(message = "Quiz title is required")
    private String title;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @Min(value = 0, message = "Passing score must be at least 0")
    @Max(value = 100, message = "Passing score must be at most 100")
    private Integer passingScore;

    @NotEmpty(message = "Quiz must contain at least one question")
    @Valid
    private List<QuestionDto> questions;

    // Геттеры и сеттеры
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public Integer getPassingScore() { return passingScore; }
    public void setPassingScore(Integer passingScore) { this.passingScore = passingScore; }

    // Обратная совместимость при переходе со старого поля passScore
    public Integer getPassScore() { return this.passingScore; }
    public void setPassScore(Integer passScore) { this.passingScore = passScore; }

    public List<QuestionDto> getQuestions() { return questions; }
    public void setQuestions(List<QuestionDto> questions) { this.questions = questions; }
}
