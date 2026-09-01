package com.rag.ragbackend.auth.service;

import com.rag.ragbackend.auth.dto.AuthResponse;
import com.rag.ragbackend.auth.dto.LoginRequest;
import com.rag.ragbackend.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    public AuthServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return userService.login(request.getEmail(), request.getPassword());
    }
}
