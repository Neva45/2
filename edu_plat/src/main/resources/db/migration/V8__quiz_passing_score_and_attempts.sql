-- 1. Добавляем исходное поле pass_score (если его ещё нет) и новое поле passing_score
ALTER TABLE quizzes ADD COLUMN IF NOT EXISTS pass_score INTEGER DEFAULT 50;
ALTER TABLE quizzes ADD COLUMN IF NOT EXISTS passing_score INTEGER;

-- 2. Переносим данные из pass_score в passing_score (фаза Expand)
UPDATE quizzes SET passing_score = pass_score WHERE passing_score IS NULL;

-- 3. Создаём таблицу попыток прохождения тестов для расчёта доли успешных сдач
CREATE TABLE quiz_attempts (
    id BIGSERIAL PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    passed BOOLEAN NOT NULL,
    CONSTRAINT fk_attempt_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
);