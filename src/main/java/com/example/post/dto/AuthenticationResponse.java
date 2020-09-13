package com.example.post.dto;

public class AuthenticationResponse {

    private String userName;
    private String AuthenticationToken;

    
    public AuthenticationResponse() {}

    public AuthenticationResponse(String userName, String authenticationToken) {
        this.userName = userName;
        AuthenticationToken = authenticationToken;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAuthenticationToken() {
        return AuthenticationToken;
    }

    public void setAuthenticationToken(String authenticationToken) {
        AuthenticationToken = authenticationToken;
    }

}
