package com.example.calculator;

public class QuizScoreEvaluator {

    /**
     * Проверяет, достигнут ли проходной балл (в процентах от 0 до 100).
     */
    public boolean isPassed(int totalQuestions, int correctAnswers, int passingScorePercent) {
        if (passingScorePercent < 0 || passingScorePercent > 100) {
            throw new IllegalArgumentException("Passing score must be between 0 and 100");
        }
        if (totalQuestions <= 0 || correctAnswers < 0 || correctAnswers > totalQuestions) {
            return false;
        }
        double scorePercent = ((double) correctAnswers / totalQuestions) * 100.0;
        return scorePercent >= passingScorePercent;
    }
}
