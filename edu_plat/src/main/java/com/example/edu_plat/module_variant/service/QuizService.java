package com.example.edu_plat.module_variant.service;

import com.example.calculator.QuizScoreEvaluator;
import com.example.edu_plat.exception.ResourceNotFoundException;
import com.example.edu_plat.module_catalog.api.CatalogModuleApi;
import com.example.edu_plat.module_users.api.UserModuleApi;
import com.example.edu_plat.module_variant.dto.*;
import com.example.edu_plat.module_variant.model.Question;
import com.example.edu_plat.module_variant.model.Quiz;
import com.example.edu_plat.module_variant.model.QuizAttempt;
import com.example.edu_plat.module_variant.repository.QuizAttemptRepository;
import com.example.edu_plat.module_variant.repository.QuizRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private static final Logger log = LoggerFactory.getLogger(QuizService.class);

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final CatalogModuleApi catalogModuleApi;
    private final UserModuleApi userModuleApi;

    public QuizService(QuizRepository quizRepository,
                       QuizAttemptRepository quizAttemptRepository,
                       CatalogModuleApi catalogModuleApi,
                       UserModuleApi userModuleApi) {
        this.quizRepository = quizRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.catalogModuleApi = catalogModuleApi;
        this.userModuleApi = userModuleApi;
    }

    @Transactional
    public QuizResponse createQuiz(QuizRequest request, String teacherEmail) {
        Long teacherId = userModuleApi.getUserIdByEmail(teacherEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found: " + teacherEmail));

        if (!catalogModuleApi.courseExists(request.getCourseId())) {
            throw new ResourceNotFoundException("Course not found with id: " + request.getCourseId());
        }

        if (!userModuleApi.isAdmin(teacherId)
                && !catalogModuleApi.isCourseTeacher(teacherId, request.getCourseId())) {
            throw new AccessDeniedException("You can create quizzes only for your own courses");
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(request.getTitle());
        quiz.setCourseId(request.getCourseId());
        
        // Устанавливаем проходной балл (по умолчанию 50%, если не передан в запросе)
        int targetScore = request.getPassingScore() != null ? request.getPassingScore() : 50;
        quiz.setPassingScore(targetScore);

        List<Question> questions = request.getQuestions().stream().map(dto -> {
            Question q = new Question();
            q.setText(dto.getText());
            q.setCorrectAnswer(dto.getCorrectAnswer());
            q.setQuiz(quiz);
            return q;
        }).collect(Collectors.toList());

        quiz.setQuestions(questions);
        Quiz saved = quizRepository.save(quiz);
        log.info("Quiz created id={}, courseId={}, passingScore={}, teacherId={}", 
                saved.getId(), request.getCourseId(), saved.getPassingScore(), teacherId);
        return toResponse(saved);
    }

    @Transactional
    public QuizResultResponse submitQuiz(Long quizId, QuizSubmissionRequest submission) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));

        List<Question> questions = quiz.getQuestions();
        int total = questions.size();
        int correctCount = 0;

        Map<Long, String> studentAnswers = submission.getAnswers();

        for (Question q : questions) {
            String studentAnswer = studentAnswers.get(q.getId());
            if (studentAnswer != null && studentAnswer.equalsIgnoreCase(q.getCorrectAnswer())) {
                correctCount++;
            }
        }

        // Расчёт достижения проходного балла через внешнюю библиотеку
        QuizScoreEvaluator evaluator = new QuizScoreEvaluator();
        int passingScore = quiz.getPassingScore() != null ? quiz.getPassingScore() : 50;
        boolean passed = evaluator.isPassed(total, correctCount, passingScore);

        // Сохраняем результат попытки в БД для расчёта доли успешных попыток в отчёте
        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuizId(quizId);
        attempt.setPassed(passed);
        quizAttemptRepository.save(attempt);

        log.info("Quiz {} submitted: {}/{} correct, passingScore={}, passed={}", 
                quizId, correctCount, total, passingScore, passed);
        return new QuizResultResponse(total, correctCount, passed);
    }

    @Transactional(readOnly = true)
    public QuizResponse getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + id));
        return toResponse(quiz);
    }

    private QuizResponse toResponse(Quiz quiz) {
        QuizResponse response = new QuizResponse();
        response.setId(quiz.getId());
        response.setTitle(quiz.getTitle());
        response.setCourseId(quiz.getCourseId());
        response.setPassingScore(quiz.getPassingScore());
        response.setQuestions(quiz.getQuestions().stream().map(q -> {
            QuestionResponse qr = new QuestionResponse();
            qr.setId(q.getId());
            qr.setText(q.getText());
            return qr;
        }).collect(Collectors.toList()));
        return response;
    }
}
