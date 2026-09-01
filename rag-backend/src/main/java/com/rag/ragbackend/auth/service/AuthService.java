package com.rag.ragbackend.auth.service;

import com.rag.ragbackend.auth.dto.AuthResponse;
import com.rag.ragbackend.auth.dto.LoginRequest;

public interface AuthService {

    AuthResponse login(LoginRequest request);
}
