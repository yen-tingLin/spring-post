package com.example.post.controller;

import javax.validation.Valid;

import com.example.post.dto.AuthenticationResponse;
import com.example.post.dto.LoginRequest;
import com.example.post.dto.RefreshTokenRequest;
import com.example.post.dto.RegisterRequest;
import com.example.post.service.AuthService;
import com.example.post.service.RefreshTokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(value = "http://localhost:4200", maxAge = 3600L)
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    // constructor injection
    @Autowired
    AuthController(AuthService authService,
            RefreshTokenService refreshTokenService) 
    {
        //Objects.requireNonNull(userService);
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) 
    {
        authService.register(registerRequest);
        return new ResponseEntity<>("please check email and activate your acount", HttpStatus.OK);
    }

    @GetMapping("accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token) 
    {
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account activated successfully", HttpStatus.OK);
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest) 
    {
        return authService.login(loginRequest);
    }

    @PostMapping("/refresh/token")
    public AuthenticationResponse refreshTokens(
                @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) 
    {
        return authService.refreshToken(refreshTokenRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
                @Valid @RequestBody RefreshTokenRequest refreshTokenRequest) 
    {
        refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());            
        return ResponseEntity.status(HttpStatus.OK)
                            .body("Refresh token deleted successfully");
        
    }

}
