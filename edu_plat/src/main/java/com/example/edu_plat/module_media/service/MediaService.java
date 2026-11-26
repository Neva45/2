package com.example.edu_plat.module_media.service;

import com.example.edu_plat.exception.InvalidMediaFormatException;
import com.example.edu_plat.module_media.dto.MediaResponse; // Убедись, что он у тебя есть
import com.example.edu_plat.module_media.model.MediaFile;
import com.example.edu_plat.module_media.repository.MediaFileRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class MediaService {

    private final MediaFileRepository mediaFileRepository;
    private final Path rootLocation = Paths.get("uploads");

    public MediaService(MediaFileRepository mediaFileRepository) {
        this.mediaFileRepository = mediaFileRepository;
    }

    @Transactional
    public MediaFile upload(MultipartFile file) {
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new InvalidMediaFormatException("Размер файла превышает 5 МиБ");
        }

        try {
            byte[] bytes = file.getBytes();
            String mimeType = detectMimeType(bytes);
            
            if (mimeType == null) {
                throw new InvalidMediaFormatException("Неподдерживаемый формат. Разрешены только PNG, JPEG, WEBP.");
            }

            String extension = mimeType.split("/")[1];
            String serverFileName = UUID.randomUUID().toString() + "." + extension;

            Files.createDirectories(rootLocation);
            Path destinationFile = rootLocation.resolve(serverFileName);
            Files.copy(file.getInputStream(), destinationFile);

            MediaFile mediaFile = new MediaFile();
            mediaFile.setServerFileName(serverFileName);
            mediaFile.setMimeType(mimeType);
            mediaFile.setSize(file.getSize());
            mediaFile.setOriginalName(file.getOriginalFilename()); // Сохраняем оригинальное имя для истории

            try {
                return mediaFileRepository.save(mediaFile);
            } catch (Exception e) {
                Files.deleteIfExists(destinationFile); // Откат
                throw new RuntimeException("Ошибка сохранения в БД, файл удален", e);
            }

        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении/сохранении файла", e);
        }
    }

    // 1. Получение метаданных из БД
    public MediaFile getMetadata(Long id) {
        return mediaFileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Файл не найден"));
    }

    // 2. Загрузка самого файла с диска
    public Resource loadFile(String fileName) {
        try {
            Path file = rootLocation.resolve(fileName);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Не удалось прочитать файл");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Ошибка чтения файла", e);
        }
    }

    // 3. Маппинг в DTO для ответа
    public MediaResponse mapToResponse(MediaFile mediaFile) {
        MediaResponse response = new MediaResponse();
        response.setId(mediaFile.getId());
        response.setFileName(mediaFile.getServerFileName());
        response.setMimeType(mediaFile.getMimeType());
        response.setSize(mediaFile.getSize());
        return response;
    }

    private String detectMimeType(byte[] bytes) {
        if (bytes.length < 12) return null;
        if (bytes[0] == (byte) 0x89 && bytes[1] == (byte) 0x50 && bytes[2] == (byte) 0x4E && bytes[3] == (byte) 0x47) return "image/png";
        if (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xD8 && bytes[2] == (byte) 0xFF) return "image/jpeg";
        if (bytes[0] == 'R' && bytes[1] == 'I' && bytes[2] == 'F' && bytes[3] == 'F' &&
            bytes[8] == 'W' && bytes[9] == 'E' && bytes[10] == 'B' && bytes[11] == 'P') return "image/webp";
        return null;
    }
}