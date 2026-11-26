package com.example.edu_plat.module_catalog.service;

import com.example.edu_plat.exception.ResourceNotFoundException;
import com.example.edu_plat.module_catalog.dto.CourseReportProjection;
import com.example.edu_plat.module_catalog.dto.CourseRequest;
import com.example.edu_plat.module_catalog.dto.CourseResponse;
import com.example.edu_plat.module_catalog.mapper.CourseMapper;
import com.example.edu_plat.module_catalog.model.Course;
import com.example.edu_plat.module_catalog.repository.CourseCriteriaRepository;
import com.example.edu_plat.module_catalog.repository.CourseRepository;
import com.example.edu_plat.module_catalog.repository.specification.CourseSpecifications;
import com.example.edu_plat.module_catalog.utils.SearchUtils;
import com.example.edu_plat.module_users.api.UserModuleApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseService.class);

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final CourseCriteriaRepository courseCriteriaRepository;
    private final UserModuleApi userModuleApi;

    public CourseService(CourseRepository courseRepository,
                        UserModuleApi userModuleApi,
                        CourseMapper courseMapper,
                        CourseCriteriaRepository courseCriteriaRepository) {
        this.courseRepository = courseRepository;
        this.userModuleApi = userModuleApi;
        this.courseMapper = courseMapper;
        this.courseCriteriaRepository = courseCriteriaRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.REPEATABLE_READ)
    public CourseResponse createCourse(CourseRequest request, String teacherEmail) {
        // Получаем ID преподавателя через публичный API модуля пользователей
        Long teacherId = userModuleApi.getUserIdByEmail(teacherEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found: " + teacherEmail));

        Course course = courseMapper.toEntity(request);
        course.setTeacherId(teacherId);

        Course saved = courseRepository.save(course);
        log.info("Course created id={}, teacherId={}", saved.getId(), teacherId);
        return courseMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(courseMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return courseMapper.toResponse(course);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public CourseResponse updateCourseWithLock(Long id, CourseRequest request) {
        Course course = courseRepository.findByIdWithPessimisticLock(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));

        course.setTitle(request.getTitle());

        Course updated = courseRepository.save(course);
        log.info("Course updated with pessimistic lock id={}", updated.getId());
        return courseMapper.toResponse(updated);
    }
    
    @Transactional(readOnly = true)
    public List<CourseReportProjection> getCoursesReport() {
        return courseRepository.getCoursesReport();
    }

    // 7 и 8 пункты: Поиск через Spring Data JPA Specification с пагинацией и сортировкой
    @Transactional(readOnly = true)
    public Page<CourseResponse> searchBySpecification(
            String title, BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
        
        SearchUtils.validatePriceParams(minPrice, maxPrice);
        
        // Настройка пагинации со строгой последовательной сортировкой: сначала по price, затем по id
        Pageable pageable = PageRequest.of(
                page, size, 
                Sort.by(Course::getPrice).ascending()
                    .and(Sort.by(Course::getId).ascending())
        );
        
        Specification<Course> spec = CourseSpecifications.findByFilter(title, minPrice, maxPrice);
                
        return courseRepository.findAll(spec, pageable).map(courseMapper::toResponse);
    }

    // 7 и 8 пункты: Поиск через EntityManager Criteria API с пагинацией и сортировкой
    @Transactional(readOnly = true)
    public Page<CourseResponse> searchByCriteria(
            String title, BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
        
        SearchUtils.validatePriceParams(minPrice, maxPrice);
        
        // Настройка пагинации (сама сортировка зашита внутри Criteria-запроса)
        Pageable pageable = PageRequest.of(page, size);
        
        return courseCriteriaRepository.searchCourses(title, minPrice, maxPrice, pageable).map(courseMapper::toResponse);
    }
}
