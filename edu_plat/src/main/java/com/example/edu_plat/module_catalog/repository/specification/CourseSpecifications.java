package com.example.edu_plat.module_catalog.repository.specification;

import com.example.edu_plat.module_catalog.model.Course;
import com.example.edu_plat.module_catalog.utils.SearchUtils;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CourseSpecifications {

    public static Specification<Course> findByFilter(String title, BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.trim().isEmpty()) {
                String escapedTitle = SearchUtils.escapeLikePattern(title);
                // Поиск без учета регистра (LOWER) + экранирование (\)
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

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
