package com.example.edu_plat.module_media.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "media_files")
public class MediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "server_file_name", nullable = false)
    private String serverFileName;

    @Column(name = "mime_type", nullable = false)
    private String mimeType;

    @Column(nullable = false)
    private Long size;

    @Column(name = "original_name")
    private String originalName;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt = LocalDateTime.now();

    // Пустой конструктор
    public MediaFile() {}

    // --- Геттеры и сеттеры ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getServerFileName() { return serverFileName; }
    public void setServerFileName(String serverFileName) { this.serverFileName = serverFileName; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }

    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}