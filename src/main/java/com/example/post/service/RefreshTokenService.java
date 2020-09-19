package com.example.post.service;

import com.example.post.model.RefreshToken;


public interface RefreshTokenService {
    
    public RefreshToken generateRefreshToken();
    public void validateRefreshToken(String token);
    public void deleteRefreshToken(String token);

}
