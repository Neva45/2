package com.example.edu_plat.module_users.api;

import java.util.Optional;

public interface UserModuleApi {
    boolean existsById(Long userId);
    Optional<Long> getUserIdByEmail(String email);
    boolean isAdmin(Long userId);
}
