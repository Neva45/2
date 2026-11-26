package com.example.edu_plat.module_media.service;

import com.example.edu_plat.exception.ResourceNotFoundException;
import com.example.edu_plat.module_media.dto.MediaResponse;
import com.example.edu_plat.module_media.model.MediaFile;
import com.example.edu_plat.module_media.repository.MediaFileRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class MediaService {

    private final MediaFileRepository mediaFileRepository;
    private final Path rootLocation = Paths.get("upload-dir");

    public MediaService(MediaFileRepository mediaFileRepository) {
        this.mediaFileRepository = mediaFileRepository;
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директорию для хранения файлов", e);
        }
    }

    // Аннотация гарантирует откат в БД, если сохранение метаданных упадет
    @Transactional(rollbackFor = Exception.class)
    public MediaResponse uploadFile(MultipartFile file) {
        // 1. Валидация размера (5 МиБ)
        long maxSizeBytes = 5 * 1024 * 1024;
        if (file.getSize() > maxSizeBytes) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Размер файла превышает допустимые 5 МиБ");
        }

        // 2. Валидация формата по Magic Bytes
        String contentType = detectMimeTypeByContent(file);
        if (contentType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Допустимы только форматы PNG, JPEG и WEBP");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) originalFilename = "unknown";
        
        String fileExtension = getExtension(originalFilename);
        String storageName = UUID.randomUUID().toString() + (fileExtension.isEmpty() ? "" : "." + fileExtension);
        Path targetPath = this.rootLocation.resolve(storageName);

        boolean fileSavedToDisk = false;

        try {
            // 3. Пытаемся физически сохранить файл на диск
            Files.copy(file.getInputStream(), targetPath);
            fileSavedToDisk = true;

            // 4. Запись метаданных в БД
            MediaFile mediaFile = new MediaFile();
            mediaFile.setStorageName(storageName);
            mediaFile.setOriginalName(originalFilename);
            mediaFile.setContentType(contentType);
            mediaFile.setFileSize(file.getSize());

            // saveAndFlush принудительно отправляет данные в Postgres прямо сейчас,
            // чтобы поймать любые констреинты БД (например, дубликат unique ключа UUID)
            MediaFile saved = mediaFileRepository.saveAndFlush(mediaFile);
            return mapToResponse(saved);

        } catch (Exception e) {
            // 5. ОБРАБОТКА СБОЯ: если файл успел записаться на диск, но запись в БД
            // выкинула ошибку — немедленно удаляем незавершенный файл с диска
            if (fileSavedToDisk) {
                try {
                    Files.deleteIfExists(targetPath);
                } catch (IOException ioException) {
                    // Логируем, если файл заблокирован ОС
                    System.err.println("Критическая ошибка: не удалось удалить мусорный файл " + targetPath);
                }
            }
            
            // Пробрасываем ошибку дальше, чтобы Spring зафиксировал падение и откатил транзакцию в БД
            if (e instanceof ResponseStatusException) {
                throw (ResponseStatusException) e;
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка при обработке и сохранении медиафайла", e);
        }
    }

    @Transactional(readOnly = true)
    public MediaFile getMetadata(Long id) {
        return mediaFileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Файл не найден с id: " + id));
    }

    @Transactional(readOnly = true)
    public Path loadFile(String storageName) {
        return rootLocation.resolve(storageName);
    }

    private String detectMimeTypeByContent(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            byte[] header = new byte[12];
            int read = is.read(header);
            if (read < 4) return null;

            if (header[0] == (byte) 0x89 && header[1] == (byte) 0x50 && header[2] == (byte) 0x4E && header[3] == (byte) 0x47) {
                return "image/png";
            }
            if (header[0] == (byte) 0xFF && header[1] == (byte) 0xD8 && header[2] == (byte) 0xFF) {
                return "image/jpeg";
            }
            if (read >= 12 && header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F'
                    && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P') {
                return "image/webp";
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    private String getExtension(String filename) {
        int index = filename.lastIndexOf('.');
        return index == -1 ? "" : filename.substring(index + 1);
    }

    public MediaResponse mapToResponse(MediaFile mediaFile) {
        MediaResponse response = new MediaResponse();
        response.setId(mediaFile.getId());
        response.setOriginalName(mediaFile.getOriginalName());
        response.setContentType(mediaFile.getContentType());
        response.setFileSize(mediaFile.getFileSize());
        return response;
    }
}
