package com.example.edu_plat.module_lessons.service;

import com.example.edu_plat.module_catalog.api.CatalogModuleApi;
import com.example.edu_plat.exception.ResourceNotFoundException;
import com.example.edu_plat.module_lessons.dto.LessonRequest;
import com.example.edu_plat.module_lessons.dto.LessonResponse;
import com.example.edu_plat.module_lessons.model.Lesson;
import com.example.edu_plat.module_lessons.repository.LessonRepository;
import com.example.edu_plat.module_users.api.CustomUserDetails;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final CatalogModuleApi catalogModuleApi; // Общаемся со смежным модулем строго через API

    public LessonService(LessonRepository lessonRepository, CatalogModuleApi catalogModuleApi) {
        this.lessonRepository = lessonRepository;
        this.catalogModuleApi = catalogModuleApi;
    }

    @Transactional
    public LessonResponse createLesson(LessonRequest request, CustomUserDetails currentUser) {
        // Проверяем, существует ли курс, через API каталога
        if (!catalogModuleApi.courseExists(request.getCourseId())) {
            throw new ResourceNotFoundException("Course not found with id: " + request.getCourseId());
        }

        // Проверка прав доступа: ADMIN или TEACHER, являющийся автором курса
        validateAccess(request.getCourseId(), currentUser);

        Lesson lesson = new Lesson();
        lesson.setCourseId(request.getCourseId());
        lesson.setTitle(request.getTitle());
        lesson.setContent(request.getContent());

        Lesson saved = lessonRepository.save(lesson);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public LessonResponse getLessonById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + id));
        return mapToResponse(lesson);
    }

    @Transactional
    public LessonResponse updateLesson(Long id, LessonRequest request, CustomUserDetails currentUser) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + id));

        // Запрещаем менять ID курса у существующего урока во избежание путаницы прав
        if (!lesson.getCourseId().equals(request.getCourseId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot change course ID for existing lesson");
        }

        validateAccess(lesson.getCourseId(), currentUser);

        lesson.setTitle(request.getTitle());
        lesson.setContent(request.getContent());

        try {
            // При вызове save() Hibernate проверит поле @Version
            Lesson updated = lessonRepository.saveAndFlush(lesson);
            return mapToResponse(updated);
        } catch (jakarta.persistence.OptimisticLockException | org.springframework.orm.ObjectOptimisticLockingFailureException e) {
            // В случае конфликта версий возвращаем HTTP 409 Conflict
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Данные были изменены другим пользователем. Обновите страницу.");
        }
    }

    private void validateAccess(Long courseId, CustomUserDetails currentUser) {
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        if (isAdmin) return;

        boolean isTeacher = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TEACHER"));

        // Запрашиваем у модуля каталога информацию, является ли пользователь автором курса
        if (!isTeacher || !catalogModuleApi.isCourseTeacher(currentUser.getId(), courseId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Вы не имеете прав на управление уроками этого курса");
        }
    }

    private LessonResponse mapToResponse(Lesson lesson) {
        LessonResponse response = new LessonResponse();
        response.setId(lesson.getId());
        response.setCourseId(lesson.getCourseId());
        response.setTitle(lesson.getTitle());
        response.setContent(lesson.getContent());
        response.setVersion(lesson.getVersion());
        return response;
    }
}
