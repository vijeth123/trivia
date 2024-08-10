package com.santander.trivia.exception;

public class TriviaNotFoundException extends RuntimeException {
    public TriviaNotFoundException(String message) {
        super(message);
    }
}