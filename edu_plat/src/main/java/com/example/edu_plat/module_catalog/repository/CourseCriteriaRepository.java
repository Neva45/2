package com.example.edu_plat.module_catalog.repository;

import com.example.edu_plat.module_catalog.model.Course;
import com.example.edu_plat.module_catalog.utils.SearchUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CourseCriteriaRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Page<Course> searchCourses(String title, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        // 1. Основной запрос для получения данных
        CriteriaQuery<Course> cq = cb.createQuery(Course.class);
        Root<Course> root = cq.from(Course.class);
        
        // Навешиваем предикаты фильтрации
        List<Predicate> predicates = buildPredicates(cb, root, title, minPrice, maxPrice);
        cq.where(predicates.toArray(new Predicate[0]));
        
        // Добавляем обязательную сортировку: сначала по price, потом по id
        cq.orderBy(cb.asc(root.get("price")), cb.asc(root.get("id")));

        TypedQuery<Course> typedQuery = entityManager.createQuery(cq);
        
        // Пагинация данных
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<Course> courses = typedQuery.getResultList();

        // 2. Дополнительный запрос для подсчета общего числа строк (требуется для PageImpl)
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Course> countRoot = countQuery.from(Course.class);
        List<Predicate> countPredicates = buildPredicates(cb, countRoot, title, minPrice, maxPrice);
        countQuery.select(cb.count(countRoot)).where(countPredicates.toArray(new Predicate[0]));
        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(courses, pageable, totalCount);
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Course> root, String title, BigDecimal minPrice, BigDecimal maxPrice) {
        List<Predicate> predicates = new ArrayList<>();

        if (title != null && !title.trim().isEmpty()) {
            String escapedTitle = SearchUtils.escapeLikePattern(title);
            predicates.add(cb.like(
                cb.lower(root.get("title")), 
                "%" + escapedTitle.toLowerCase() + "%", 
                '\\'
            ));
        }
        if (minPrice != null) {
            predicates.add(cb.ge(root.get("price"), minPrice));
        }
        if (maxPrice != null) {
            predicates.add(cb.le(root.get("price"), maxPrice));
        }
        return predicates;
    }
}
