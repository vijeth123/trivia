package com.santander.trivia.service;

import com.santander.trivia.constant.TriviaReply;
import com.santander.trivia.exception.TriviaNotFoundException;
import com.santander.trivia.model.Trivia;
import com.santander.trivia.model.TriviaApiResponse;
import com.santander.trivia.model.TriviaDto;
import com.santander.trivia.model.TriviaQuestion;
import com.santander.trivia.repository.TriviaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TriviaServiceTest {

    @Mock
    private TriviaRepository triviaRepository;

    @Mock
    private RestTemplate restTemplate;

    private TriviaService triviaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        triviaService = new TriviaService(triviaRepository, restTemplate, "https://opentdb.com/api.php?amount=1", 3);
    }

    @Test
    void startTrivia_shouldReturnTriviaDto() {
        // Mocking the external API response
        TriviaApiResponse apiResponse = new TriviaApiResponse();
        TriviaQuestion question = new TriviaQuestion();
        question.setQuestion("What is 2+2?");
        question.setCorrectAnswer("4");
        question.setIncorrectAnswers(Arrays.asList("3", "5", "6"));
        apiResponse.setResults(Arrays.asList(question));

        when(restTemplate.getForObject(any(String.class), eq(TriviaApiResponse.class))).thenReturn(apiResponse);

        // Mocking the save operation
        Trivia savedTrivia = new Trivia();
        savedTrivia.setTriviaId(1L);
        savedTrivia.setQuestion("What is 2+2?");
        savedTrivia.setCorrectAnswer("4");
        savedTrivia.setAnswerAttempts(0);

        when(triviaRepository.save(any(Trivia.class))).thenReturn(savedTrivia);

        // Call the method under test
        TriviaDto result = triviaService.startTrivia();

        // Verify the result
        assertEquals(1L, result.getTriviaId());
        assertEquals("What is 2+2?", result.getQuestion());
        assertEquals(4, result.getPossibleAnswers().size()); // 1 correct + 3 incorrect

        verify(triviaRepository, times(1)).save(any(Trivia.class));
    }

    @Test
    void replyToTrivia_correctAnswer_shouldReturnRight() {
        // Mocking the database retrieval
        Trivia trivia = new Trivia();
        trivia.setTriviaId(1L);
        trivia.setQuestion("What is 2+2?");
        trivia.setCorrectAnswer("4");
        trivia.setAnswerAttempts(0);

        when(triviaRepository.findById(1L)).thenReturn(Optional.of(trivia));

        // Call the method under test
        TriviaReply result = triviaService.replyToTrivia(1L, "4");

        // Verify the result
        assertEquals(TriviaReply.RIGHT, result);
        verify(triviaRepository, times(1)).delete(trivia);
    }

    @Test
    void replyToTrivia_wrongAnswer_shouldReturnWrong() {
        // Mocking the database retrieval
        Trivia trivia = new Trivia();
        trivia.setTriviaId(1L);
        trivia.setQuestion("What is 2+2?");
        trivia.setCorrectAnswer("4");
        trivia.setAnswerAttempts(0);

        when(triviaRepository.findById(1L)).thenReturn(Optional.of(trivia));

        // Call the method under test
        TriviaReply result = triviaService.replyToTrivia(1L, "5");

        // Verify the result
        assertEquals(TriviaReply.WRONG, result);
        assertEquals(1, trivia.getAnswerAttempts());
        verify(triviaRepository, times(1)).save(trivia);
    }

    @Test
    void replyToTrivia_maxAttempts_shouldReturnMaxAttemptsReached() {
        // Mocking the database retrieval
        Trivia trivia = new Trivia();
        trivia.setTriviaId(1L);
        trivia.setQuestion("What is 2+2?");
        trivia.setCorrectAnswer("4");
        trivia.setAnswerAttempts(3);

        when(triviaRepository.findById(1L)).thenReturn(Optional.of(trivia));

        // Call the method under test
        TriviaReply result = triviaService.replyToTrivia(1L, "5");

        // Verify the result
        assertEquals(TriviaReply.MAX_ATTEMPTS_REACHED, result);
    }

    @Test
    void replyToTrivia_questionNotFound_shouldThrowException() {
        // Mocking the database retrieval
        when(triviaRepository.findById(1L)).thenReturn(Optional.empty());

        // Call the method under test
        TriviaNotFoundException thrown = assertThrows(TriviaNotFoundException.class, () -> {
            triviaService.replyToTrivia(1L, "4");
        });

        assertEquals("No such question!", thrown.getMessage());
    }
}
