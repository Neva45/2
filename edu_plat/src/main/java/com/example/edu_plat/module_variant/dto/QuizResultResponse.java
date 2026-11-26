package com.example.edu_plat.module_variant.dto;

public class QuizResultResponse {
    private int totalQuestions;
    private int correctAnswers;
    private boolean passed;

    public QuizResultResponse(int totalQuestions, int correctAnswers, boolean passed) {
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.passed = passed;
    }

    // Геттеры
    public int getTotalQuestions() { return totalQuestions; }
    public int getCorrectAnswers() { return correctAnswers; }
    public boolean isPassed() { return passed; }
}