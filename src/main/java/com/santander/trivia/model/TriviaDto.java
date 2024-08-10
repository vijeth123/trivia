package com.santander.trivia.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TriviaDto {
    Long triviaId;
    String question;
    List<String> possibleAnswers;
}