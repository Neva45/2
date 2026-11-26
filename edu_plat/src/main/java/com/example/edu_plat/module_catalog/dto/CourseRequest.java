package com.example.edu_plat.module_catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class CourseRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must be at most 150 characters")
    private String title;

    // Валидация перенесена на новое поле summary
    @Size(max = 5000, message = "Summary must be at most 5000 characters")
    private String summary;

    private BigDecimal price;

    // Геттеры и сеттеры для основного контракта
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
