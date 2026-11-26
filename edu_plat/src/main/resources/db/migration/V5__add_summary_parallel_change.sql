-- 1. Добавляем новое поле summary, разрешая ему быть NULL на время перехода
ALTER TABLE courses ADD COLUMN summary TEXT;

-- 2. Переносим все существующие данные из description в summary
UPDATE courses SET summary = description WHERE summary IS NULL;
