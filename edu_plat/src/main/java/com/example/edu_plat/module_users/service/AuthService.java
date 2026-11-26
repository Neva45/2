package com.example.edu_plat.module_users.service;

import com.example.edu_plat.module_users.dto.LoginRequest;
import com.example.edu_plat.module_users.dto.UserRegistrationRequest;
import com.example.edu_plat.module_users.security.JwtService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService,
                       JwtService jwtService,
                       UserService userService,
                       PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(UserRegistrationRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        userService.registerUser(request, encodedPassword);
        log.info("User registration requested: {}", request.getEmail());
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public String login(LoginRequest request) {
        // Если креды неверны — AuthenticationException будет перехвачен GlobalExceptionHandler и вернёт 401
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails);
        log.info("User logged in: {}", request.getEmail());
        return token;
    }
}