package com.santander.trivia.steps;

import com.santander.trivia.model.Trivia;
import com.santander.trivia.model.TriviaAnswerRequest;
import com.santander.trivia.model.TriviaDto;
import com.santander.trivia.repository.TriviaRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TriviaSteps {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TriviaRepository triviaRepository;

    private ResponseEntity<TriviaDto> startTriviaResponse;
    private ResponseEntity<String> replyTriviaResponse;
    private Trivia currentTrivia;

    @When("I start a new trivia game")
    public void i_start_a_new_trivia_game() {
        String url = "/trivia/start";
        startTriviaResponse = restTemplate.postForEntity(url, null, TriviaDto.class);
    }

    @Then("I should receive a trivia question with possible answers")
    public void i_should_receive_a_trivia_question_with_possible_answers() {
        assertEquals(200, startTriviaResponse.getStatusCodeValue());
        assertEquals(1, triviaRepository.count());
    }

    @Given("a trivia game is started with question {string} and correct answer {string}")
    public void a_trivia_game_is_started_with_question_and_correct_answer(String question, String correctAnswer) {
        Trivia trivia = new Trivia();
        trivia.setQuestion(question);
        trivia.setCorrectAnswer(correctAnswer);
        trivia.setAnswerAttempts(0);
        currentTrivia = triviaRepository.save(trivia);
    }

    @Given("a trivia game is started with question {string} and correct answer {string} and {int} attempts made")
    public void a_trivia_game_is_started_with_question_and_correct_answer_and_attempts_made(String question, String correctAnswer, int attempts) {
        Trivia trivia = new Trivia();
        trivia.setQuestion(question);
        trivia.setCorrectAnswer(correctAnswer);
        trivia.setAnswerAttempts(attempts);
        currentTrivia = triviaRepository.save(trivia);
    }

    @When("I answer {string} to the trivia question")
    public void i_answer_to_the_trivia_question(String answer) {
        String url = "/trivia/reply/" + currentTrivia.getTriviaId();
        TriviaAnswerRequest request = new TriviaAnswerRequest();
        request.setAnswer(answer);
        HttpEntity<TriviaAnswerRequest> entity = new HttpEntity<>(request);
        replyTriviaResponse = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
    }

    @When("I answer {string} to a non-existent trivia question")
    public void i_answer_to_a_non_existent_trivia_question(String answer) {
        String url = "/trivia/reply/999";
        TriviaAnswerRequest request = new TriviaAnswerRequest();
        request.setAnswer(answer);
        HttpEntity<TriviaAnswerRequest> entity = new HttpEntity<>(request);
        replyTriviaResponse = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
    }

    @Then("I should receive the result {string}")
    public void i_should_receive_the_result(String expectedResult) {
        assertEquals(expectedResult, replyTriviaResponse.getBody().replace("{\"result\":\"", "").replace("\"}", ""));
    }
}
