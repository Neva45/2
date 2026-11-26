package com.example.edu_plat.module_catalog.mapper;

import com.example.edu_plat.module_catalog.dto.CourseRequest;
import com.example.edu_plat.module_catalog.dto.CourseResponse;
import com.example.edu_plat.module_catalog.model.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "teacherId", ignore = true)
    @Mapping(target = "version", ignore = true)
    Course toEntity(CourseRequest request);

    CourseResponse toResponse(Course course);
}