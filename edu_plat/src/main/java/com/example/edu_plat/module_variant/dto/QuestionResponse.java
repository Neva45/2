package com.example.edu_plat.module_variant.dto;

public class QuestionResponse {
    private Long id;
    private String text;
    // Правильный ответ НЕ отдаём наружу

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}