package com.santander.trivia.constant;

public enum TriviaReply {
    RIGHT ("Right!"),
    WRONG ("Wrong!"),
    MAX_ATTEMPTS_REACHED("Max attempts reached!");

    private String value;

    TriviaReply(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
