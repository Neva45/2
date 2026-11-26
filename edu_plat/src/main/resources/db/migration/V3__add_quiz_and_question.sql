CREATE TABLE quizzes (
    id         BIGSERIAL PRIMARY KEY,
    title      VARCHAR(255) NOT NULL,
    course_id  BIGINT NOT NULL,
    CONSTRAINT fk_quiz_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

CREATE TABLE questions (
    id              BIGSERIAL PRIMARY KEY,
    text            TEXT NOT NULL,
    correct_answer  VARCHAR(255) NOT NULL,
    quiz_id         BIGINT NOT NULL,
    CONSTRAINT fk_question_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE
);