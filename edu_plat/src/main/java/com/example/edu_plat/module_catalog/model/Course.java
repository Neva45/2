package com.example.edu_plat.module_catalog.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    // Оставляем старое поле для совместимости со старой версией приложения
    @Column(columnDefinition = "TEXT")
    private String description;

    // Добавляем новое поле summary
    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "instructor_id")
    private Long teacherId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Version
    private Long version;

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    // Умный геттер для description (если старая версия записала только туда)
    public String getDescription() { 
        return this.description != null ? this.description : this.summary; 
    }
    public void setDescription(String description) { 
        this.description = description; 
        this.summary = description; // Дублируем запись для новой версии
    }

    // Умный геттер для summary
    public String getSummary() { 
        return this.summary != null ? this.summary : this.description; 
    }
    public void setSummary(String summary) { 
        this.summary = summary; 
        this.description = summary; // Дублируем запись для старой версии
    }

    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
