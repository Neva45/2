package com.example.edu_plat.module_media.controller;

import com.example.edu_plat.module_media.dto.MediaResponse;
import com.example.edu_plat.module_media.model.MediaFile;
import com.example.edu_plat.module_media.service.MediaService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    // 1. Загрузка
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MediaResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        MediaFile savedFile = mediaService.upload(file);
        return ResponseEntity.ok(mediaService.mapToResponse(savedFile));
    }

    // 2. Получение метаданных
    @GetMapping("/{id}")
    public ResponseEntity<MediaResponse> getMetadata(@PathVariable Long id) {
        MediaFile mediaFile = mediaService.getMetadata(id);
        return ResponseEntity.ok(mediaService.mapToResponse(mediaFile));
    }

    // 3. Скачивание
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        MediaFile mediaFile = mediaService.getMetadata(id);
        Resource resource = mediaService.loadFile(mediaFile.getServerFileName());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mediaFile.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + mediaFile.getOriginalName() + "\"")
                .body(resource);
    }
}