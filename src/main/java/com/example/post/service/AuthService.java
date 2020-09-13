package com.example.post.service;

import com.example.post.dto.RegisterRequest;

public interface AuthService {
    
    void register(RegisterRequest registerRequest);
    void verifyAccount(String token);

}
