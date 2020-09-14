package com.example.post.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.annotation.PostConstruct;

import com.example.post.exception.SpringPostException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
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

    // we previously created token by siging private key from keyStore, and
    // now we will validate the token by using public key
    public boolean validateToken(String jwt) {
        
        try {
            Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJws(jwt);

            // test
            System.out.println("validateToken, token : " + jwt);
            System.out.println("validateToken, token parsed : " + Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJws(jwt));

            return true;

        } catch(SpringPostException e) {
            throw new SpringPostException("Token cannot be validated successfully");
        }

        

 
    }

    private PublicKey getPublicKey() {
        try {
            return keyStore.getCertificate("blog-frontend").getPublicKey();

        } catch(KeyStoreException e) {
            throw new SpringPostException("Exception occured while retrieving public key", e);
        }
    }

    public String getUserNameFromJwt(String token) {
        Claims claims = Jwts.parser()
                    .setSigningKey(getPublicKey())
                    .parseClaimsJws(token)
                    .getBody();

        // test
        System.out.println("claims.getSubject() : " + claims.getSubject());

        // we have set user name as a subject when creating a token
        return claims.getSubject();
    }
}
