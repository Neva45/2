package com.example.edu_plat.module_media.dto;

public class MediaResponse {
    private Long id;
    private String fileName;
    private String mimeType;
    private Long size;

    // Пустой конструктор
    public MediaResponse() {
    }

    // --- Геттеры и сеттеры ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }
}