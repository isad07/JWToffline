package com.jwtoffline.core;

/** Thrown when a token, key or JSON value cannot be processed. Messages are meant to be shown to the user. */
public class JwtException extends RuntimeException {
    public JwtException(String message) {
        super(message);
    }

    public JwtException(String message, Throwable cause) {
        super(message, cause);
    }
}
