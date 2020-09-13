package com.example.post.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.annotation.PostConstruct;

import com.example.post.exception.SpringPostException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;

@Service
public class JwtProvider {

    private KeyStore keyStore;

    // PostConstruct 表示此方法是在Spring實例化该Bean之後馬上執行此方法，之後才會去實例化其他Bean
    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance("JKS");
            // get a input stream from a keystore file
            InputStream resourceAsStream = getClass().getResourceAsStream("/blog-frontend.jks");
            keyStore.load(resourceAsStream,  "rootmi".toCharArray());

        } catch(KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new SpringPostException("Exception occured while loading keystore", e);
        }

    }

    public String generateToken(Authentication authentication) {
        // org.springframework.security.core.userdetails.User
        User principal = (User) authentication.getPrincipal();
        return Jwts.builder()
                    .setSubject(principal.getUsername())
                    .signWith(getPrivateKey())
                    .compact();

    }

    private PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey("blog-frontend", "rootmi".toCharArray());

        } catch(KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new SpringPostException("Exception occured while getting key from keyStore", e);
        }
    }
}
