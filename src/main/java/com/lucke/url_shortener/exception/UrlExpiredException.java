package com.lucke.url_shortener.exception;

public class UrlExpiredException extends RuntimeException{
    public UrlExpiredException(String message) {
        super(message);
    }
}
