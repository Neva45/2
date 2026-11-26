package com.example.edu_plat.module_variant.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

public class QuizSubmissionRequest {

    @NotNull(message = "Answers map is required")
    private Map<Long, String> answers;

    public Map<Long, String> getAnswers() { return answers; }
    public void setAnswers(Map<Long, String> answers) { this.answers = answers; }
}