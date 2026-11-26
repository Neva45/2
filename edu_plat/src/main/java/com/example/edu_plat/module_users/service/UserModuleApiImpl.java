package com.example.edu_plat.module_users.service;

import com.example.edu_plat.module_users.api.UserModuleApi;
import com.example.edu_plat.module_users.repository.UserRepository;

import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserModuleApiImpl implements UserModuleApi {

    private final UserRepository userRepository;

    public UserModuleApiImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public Optional<Long> getUserIdByEmail(String email) {
        return userRepository.findByEmail(email).map(user -> user.getId());
    }

    @Override
    public boolean isAdmin(Long userId) {
        return userRepository.findById(userId)
                .map(user -> "ADMIN".equalsIgnoreCase(user.getRole()))
                .orElse(false);
    }
}
