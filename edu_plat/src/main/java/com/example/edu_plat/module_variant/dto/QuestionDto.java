package com.example.edu_plat.module_variant.dto;

import jakarta.validation.constraints.NotBlank;

public class QuestionDto {
    @NotBlank(message = "Question text cannot be blank")
    private String text;

    @NotBlank(message = "Correct answer must be specified")
    private String correctAnswer;

    // Геттеры и сеттеры
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
}