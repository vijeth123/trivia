package com.santander.trivia;

import com.santander.trivia.model.Trivia;
import com.santander.trivia.model.TriviaAnswerRequest;
import com.santander.trivia.model.TriviaDto;
import com.santander.trivia.repository.TriviaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TriviaControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TriviaRepository triviaRepository;

    @Test
    void startTrivia_shouldReturnTriviaDto() {
        String url = "http://localhost:" + port + "/trivia/start";

        ResponseEntity<TriviaDto> response = restTemplate.postForEntity(url, null, TriviaDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(triviaRepository.count() > 0);
    }

    @Test
    void replyToTrivia_correctAnswer_shouldReturnRight() {
        Trivia trivia = new Trivia();
        trivia.setQuestion("What is 2+2?");
        trivia.setCorrectAnswer("4");
        trivia.setAnswerAttempts(0);
        trivia = triviaRepository.save(trivia);

        String url = "http://localhost:" + port + "/trivia/reply/" + trivia.getTriviaId();

        TriviaAnswerRequest answerRequest = new TriviaAnswerRequest();
        answerRequest.setAnswer("4");

        HttpEntity<TriviaAnswerRequest> request = new HttpEntity<>(answerRequest);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"result\":\"Right!\"}", response.getBody());
    }

    @Test
    void replyToTrivia_wrongAnswer_shouldReturnWrong() {
        Trivia trivia = new Trivia();
        trivia.setQuestion("What is 2+2?");
        trivia.setCorrectAnswer("4");
        trivia.setAnswerAttempts(0);
        trivia = triviaRepository.save(trivia);

        String url = "http://localhost:" + port + "/trivia/reply/" + trivia.getTriviaId();

        TriviaAnswerRequest answerRequest = new TriviaAnswerRequest();
        answerRequest.setAnswer("5");

        HttpEntity<TriviaAnswerRequest> request = new HttpEntity<>(answerRequest);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("{\"result\":\"Wrong!\"}", response.getBody());
        assertEquals(1, triviaRepository.findById(trivia.getTriviaId()).get().getAnswerAttempts());
    }

    @Test
    void replyToTrivia_maxAttempts_shouldReturnMaxAttemptsReached() {
        Trivia trivia = new Trivia();
        trivia.setQuestion("What is 2+2?");
        trivia.setCorrectAnswer("4");
        trivia.setAnswerAttempts(3);
        trivia = triviaRepository.save(trivia);

        String url = "http://localhost:" + port + "/trivia/reply/" + trivia.getTriviaId();

        TriviaAnswerRequest answerRequest = new TriviaAnswerRequest();
        answerRequest.setAnswer("5");

        HttpEntity<TriviaAnswerRequest> request = new HttpEntity<>(answerRequest);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("{\"result\":\"Max attempts reached!\"}", response.getBody());
    }

    @Test
    void replyToTrivia_questionNotFound_shouldReturnNotFound() {
        String url = "http://localhost:" + port + "/trivia/reply/999";

        TriviaAnswerRequest answerRequest = new TriviaAnswerRequest();
        answerRequest.setAnswer("4");

        HttpEntity<TriviaAnswerRequest> request = new HttpEntity<>(answerRequest);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("{\"result\":\"No such question!\"}", response.getBody());
    }
}
