package com.santander.trivia.model;

import lombok.Data;

import java.util.List;

@Data
public class TriviaApiResponse {
    private int responseCode;
    private List<TriviaQuestion> results;
}