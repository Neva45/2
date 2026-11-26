package com.example.edu_plat.module_variant.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "quizzes")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "pass_score")
    private Integer passScore = 50;

    @Column(name = "passing_score")
    private Integer passingScore = 50;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    // Умные геттеры и сеттеры для параллельного перехода (Parallel Change)
    public Integer getPassScore() {
        return this.passScore != null ? this.passScore : this.passingScore;
    }

    public void setPassScore(Integer passScore) {
        this.passScore = passScore;
        this.passingScore = passScore;
    }

    public Integer getPassingScore() {
        return this.passingScore != null ? this.passingScore : this.passScore;
    }

    public void setPassingScore(Integer passingScore) {
        this.passingScore = passingScore;
        this.passScore = passingScore;
    }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
}
