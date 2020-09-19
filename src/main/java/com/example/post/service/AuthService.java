package com.example.post.service;

import com.example.post.dto.AuthenticationResponse;
import com.example.post.dto.LoginRequest;
import com.example.post.dto.RefreshTokenRequest;
import com.example.post.dto.RegisterRequest;
import com.example.post.model.User;


public interface AuthService {
    
    void register(RegisterRequest registerRequest);
    void verifyAccount(String token);
	AuthenticationResponse login(LoginRequest loginRequest);
    User getCurrentUser();
	AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);

}
