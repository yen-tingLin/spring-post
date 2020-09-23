package com.example.post.service;

import java.time.Instant;
import java.util.UUID;

import com.example.post.exception.SpringPostException;
import com.example.post.model.RefreshToken;
import com.example.post.repository.RefreshTokenRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class RefreshTokenServiceImp implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    RefreshTokenServiceImp(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    @Override
    public RefreshToken generateRefreshToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setCreatedDate(Instant.now());

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    @Override
    public void validateRefreshToken(String token) {
        refreshTokenRepository.findByToken(token).orElseThrow(
                () -> new SpringPostException("Invalid refresh token"));
    }

    @Transactional
    @Override
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);

        log.info("Refresh token deleted successfully");
    }
    
}
