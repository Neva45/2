-- Добавляем недостающие колонки в таблицу медиафайлов
ALTER TABLE media_files ADD COLUMN IF NOT EXISTS server_file_name VARCHAR(255);
ALTER TABLE media_files ADD COLUMN IF NOT EXISTS mime_type VARCHAR(255);
ALTER TABLE media_files ADD COLUMN IF NOT EXISTS size BIGINT;
ALTER TABLE media_files ADD COLUMN IF NOT EXISTS original_name VARCHAR(255);
ALTER TABLE media_files ADD COLUMN IF NOT EXISTS uploaded_at TIMESTAMP;