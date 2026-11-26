package com.example.edu_plat.module_lessons.dto;

public class LessonResponse {
    private Long id;
    private Long courseId;
    private String title;
    private String content;
    private Long version;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
