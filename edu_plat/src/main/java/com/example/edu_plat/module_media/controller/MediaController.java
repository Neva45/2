package com.example.edu_plat.module_media.controller;

import com.example.edu_plat.module_media.dto.MediaResponse;
import com.example.edu_plat.module_media.model.MediaFile;
import com.example.edu_plat.module_media.service.MediaService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    // Загрузка файла
    @PostMapping("/upload")
    public ResponseEntity<MediaResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(mediaService.uploadFile(file));
    }

    // Получение метаданных
    @GetMapping("/{id}/metadata")
    public ResponseEntity<MediaResponse> getMetadata(@PathVariable Long id) {
        MediaFile mediaFile = mediaService.getMetadata(id);
        return ResponseEntity.ok(mediaService.mapToResponse(mediaFile));
    }

    // Скачивание файла
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        MediaFile mediaFile = mediaService.getMetadata(id);
        Path file = mediaService.loadFile(mediaFile.getStorageName());
        try {
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(mediaFile.getContentType()))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + mediaFile.getOriginalName() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
