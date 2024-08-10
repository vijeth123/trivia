package com.santander.trivia.exception;

import com.santander.trivia.model.ResultResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TriviaExceptionHandler {

    @ExceptionHandler(TriviaNotFoundException.class)
    public ResponseEntity<ResultResponse> handleTriviaNotFoundException(TriviaNotFoundException ex) {
        return ResponseEntity
                .status(404)
                .body(new ResultResponse(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultResponse> handleException(Exception e) {
        return ResponseEntity.status(500).body(new ResultResponse("Internal server error: " + e.getMessage()));
    }
}