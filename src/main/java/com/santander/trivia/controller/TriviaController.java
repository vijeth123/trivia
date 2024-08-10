package com.santander.trivia.controller;

import com.santander.trivia.constant.TriviaReply;
import com.santander.trivia.model.Trivia;
import com.santander.trivia.model.TriviaAnswerRequest;
import com.santander.trivia.model.TriviaDto;
import com.santander.trivia.service.TriviaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trivia")
@Slf4j
public class TriviaController {

    public static final String RESULT = "result";

    private final TriviaService triviaService;

    @Autowired
    public TriviaController(TriviaService triviaService) {
        this.triviaService = triviaService;
    }

    @PostMapping("/start")
    public ResponseEntity<TriviaDto> startTrivia() {
        log.info("Request received to start Trivia.");
        TriviaDto triviaDto = triviaService.startTrivia();
        log.info("Trivia started and returned the response: {}", triviaDto);
        return ResponseEntity.ok(triviaDto);
    }

    @PutMapping("/reply/{triviaId}")
    public ResponseEntity<Map<String, String>> replyToTrivia(@PathVariable Long triviaId, @RequestBody TriviaAnswerRequest request) {
        log.info("Request received to reply to Trivia with id: [{}], request: {}.", triviaId, request);
        TriviaReply triviaReply = triviaService.replyToTrivia(triviaId, request.getAnswer());
        log.info("For id: [{}], request: {}, the reply received is: {}", triviaId, request, triviaReply);

        if (TriviaReply.RIGHT.equals(triviaReply)) {
            return ResponseEntity.ok(Collections.singletonMap(RESULT, triviaReply.getValue()));
        } else if (TriviaReply.MAX_ATTEMPTS_REACHED.equals(triviaReply)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap(RESULT, triviaReply.getValue()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap(RESULT, triviaReply.getValue()));
        }
    }

    @GetMapping("/all")
    public List<Trivia> getAllSavedTrivias() {
        return triviaService.getAllSavedTrivias();
    }
}
