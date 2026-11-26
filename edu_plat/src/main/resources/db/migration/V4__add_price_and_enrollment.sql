-- Добавление поля price в таблицу courses
ALTER TABLE courses ADD COLUMN price DECIMAL(10, 2) NOT NULL DEFAULT 0.00;

-- Создание таблицы enrollments
CREATE TABLE enrollments (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_enrollment_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_enrollment_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_course UNIQUE (user_id, course_id)
);
