package com.santander.trivia.service;

import com.santander.trivia.constant.TriviaReply;
import com.santander.trivia.exception.TriviaFailureException;
import com.santander.trivia.exception.TriviaNotFoundException;
import com.santander.trivia.model.Trivia;
import com.santander.trivia.model.TriviaApiResponse;
import com.santander.trivia.model.TriviaDto;
import com.santander.trivia.model.TriviaQuestion;
import com.santander.trivia.repository.TriviaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TriviaService {

    private final TriviaRepository triviaRepository;

    private final RestTemplate restTemplate;

    private final String triviaApiUrl;
    private final int maxAnswerAttempts;

    @Autowired
    public TriviaService(TriviaRepository triviaRepository,
                         RestTemplate restTemplate,
                         @Value("${trivia.api.url}") String triviaApiUrl,
                         @Value("${trivia.max.answer.attempts}") int maxAnswerAttempts) {
        this.triviaRepository = triviaRepository;
        this.restTemplate = restTemplate;
        this.triviaApiUrl = triviaApiUrl;
        this.maxAnswerAttempts = maxAnswerAttempts;
    }

    public TriviaDto startTrivia() {
        // Call the external API
        TriviaApiResponse response = restTemplate.getForObject(triviaApiUrl, TriviaApiResponse.class);
        if (response == null || CollectionUtils.isEmpty(response.getResults())) {
            throw new TriviaFailureException("Failed to fetch trivia question");
        }

        TriviaQuestion triviaQuestion = response.getResults().get(0);

        // Save to the database
        Trivia trivia = new Trivia();
        trivia.setQuestion(triviaQuestion.getQuestion());
        trivia.setCorrectAnswer(triviaQuestion.getCorrectAnswer());
        trivia.setAnswerAttempts(0);
        trivia = triviaRepository.save(trivia);

        // Prepare the response
        List<String> possibleAnswers = new ArrayList<>(triviaQuestion.getIncorrectAnswers());
        possibleAnswers.add(trivia.getCorrectAnswer());
        Collections.shuffle(possibleAnswers);

        return new TriviaDto(trivia.getTriviaId(), trivia.getQuestion(), possibleAnswers);
    }

    public TriviaReply replyToTrivia(Long triviaId, String answer) {
        Trivia trivia = triviaRepository.findById(triviaId)
                .orElseThrow(() -> new TriviaNotFoundException("No such question!"));

        if (trivia.getAnswerAttempts() >= maxAnswerAttempts) {
            return TriviaReply.MAX_ATTEMPTS_REACHED;
        }

        if (trivia.getCorrectAnswer().equalsIgnoreCase(answer)) {
            triviaRepository.delete(trivia);
            return TriviaReply.RIGHT;
        } else {
            trivia.setAnswerAttempts(trivia.getAnswerAttempts() + 1);
            triviaRepository.save(trivia);
            return trivia.getAnswerAttempts() >= maxAnswerAttempts ? TriviaReply.MAX_ATTEMPTS_REACHED : TriviaReply.WRONG;
        }
    }

    public List<Trivia> getAllSavedTrivias() {
        return triviaRepository.findAll();
    }
}