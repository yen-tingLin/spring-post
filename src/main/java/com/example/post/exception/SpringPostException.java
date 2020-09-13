package com.example.post.exception;

public class SpringPostException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public SpringPostException(String message, Exception e) {
        super(message, e);
    }

    public SpringPostException(String message) {
        super(message);
    }
    
}
