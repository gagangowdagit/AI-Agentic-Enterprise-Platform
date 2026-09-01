package com.rag.ragbackend.service;

import com.rag.ragbackend.auth.dto.AuthResponse;
import com.rag.ragbackend.entity.User;

import java.util.Optional;

public interface UserService {

    User createUser(User user);

    Optional<User> findByEmail(String email);

    AuthResponse login(String email, String password);
}