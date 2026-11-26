package com.example.edu_plat.module_users.mapper;

import com.example.edu_plat.module_users.dto.UserRegistrationRequest;
import com.example.edu_plat.module_users.dto.UserResponse;
import com.example.edu_plat.module_users.model.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toEntity(UserRegistrationRequest request);

    UserResponse toResponse(User user);
}