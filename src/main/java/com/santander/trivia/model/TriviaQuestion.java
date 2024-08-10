package com.santander.trivia.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@NoArgsConstructor
public class TriviaQuestion {
    private String category;
    private String type;
    private String difficulty;
    private String question;

    @Setter(AccessLevel.NONE)
    private String correctAnswer;

    @Setter(AccessLevel.NONE)
    private List<String> incorrectAnswers;

    public TriviaQuestion(String question, String correctAnswer) {
        this.question = question;
        this.correctAnswer = correctAnswer;
    }

    @JsonProperty("correct_answer")
    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    @JsonProperty("incorrect_answers")
    public void setIncorrectAnswers(List<String> incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }

}
