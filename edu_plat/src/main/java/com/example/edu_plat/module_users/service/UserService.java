package com.example.edu_plat.module_users.service;

import com.example.edu_plat.exception.ResourceNotFoundException;
import com.example.edu_plat.module_users.dto.UserRegistrationRequest;
import com.example.edu_plat.module_users.dto.UserResponse;
import com.example.edu_plat.module_users.mapper.UserMapper;
import com.example.edu_plat.module_users.model.User;
import com.example.edu_plat.module_users.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public User registerUser(UserRegistrationRequest request, String encodedPassword) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email " + request.getEmail() + " already exists");
        }

        User user = userMapper.toEntity(request);
        user.setPassword(encodedPassword);

        // Разрешаем только STUDENT / TEACHER, значение по умолчанию — STUDENT
        String role = request.getRole();
        if (role == null || role.isBlank()) {
            role = "STUDENT";
        }
        user.setRole(role);

        User saved = userRepository.save(user);
        log.info("Registered new user id={}, email={}, role={}", saved.getId(), saved.getEmail(), saved.getRole());
        return saved;
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserResponseByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }
}