-- Удаляем старое поле description, так как данные уже перенесены в summary
ALTER TABLE courses DROP COLUMN description;