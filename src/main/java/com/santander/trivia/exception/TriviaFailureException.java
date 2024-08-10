package com.santander.trivia.exception;

public class TriviaFailureException extends RuntimeException {
    public TriviaFailureException(String message) {
        super(message);
    }
}