CREATE TABLE media_files (
    id BIGSERIAL PRIMARY KEY,
    storage_name VARCHAR(255) NOT NULL UNIQUE, -- Имя UUID на сервере
    original_name VARCHAR(255) NOT NULL,       -- Исходное имя для скачивания
    content_type VARCHAR(100) NOT NULL,       -- image/png, image/jpeg, image/webp
    file_size BIGINT NOT NULL
);
