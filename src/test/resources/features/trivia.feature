Feature: Trivia Game

  Scenario: Starting a new trivia game
    When I start a new trivia game
    Then I should receive a trivia question with possible answers

  Scenario: Answering a trivia question correctly
    Given a trivia game is started with question "What is 2+2?" and correct answer "4"
    When I answer "4" to the trivia question
    Then I should receive the result "Right!"

  Scenario: Answering a trivia question incorrectly
    Given a trivia game is started with question "What is 2+2?" and correct answer "4"
    When I answer "5" to the trivia question
    Then I should receive the result "Wrong!"

  Scenario: Exceeding the maximum number of attempts
    Given a trivia game is started with question "What is 2+2?" and correct answer "4" and 3 attempts made
    When I answer "5" to the trivia question
    Then I should receive the result "Max attempts reached!"

  Scenario: Answering a non-existent trivia question
    When I answer "4" to a non-existent trivia question
    Then I should receive the result "No such question!"
