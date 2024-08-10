# Trivia Game REST API

This project is a simple Trivia Game REST API built using Java and Spring Boot. The API allows users to start a trivia game, answer trivia questions, and handle the game logic such as checking answers, tracking attempts, and managing game state.

## Features

- Start a new trivia game with a random question.
- Submit an answer to a trivia question.
- Track the number of answer attempts.
- Handle incorrect answers and limit attempts.
- Support for BDD testing using Cucumber.
- Integration with a public trivia API for fetching questions.

## Prerequisites

- Java 11 or higher
- Maven 3.x
- Spring Boot 2.5.x

## Getting Started

### Build the Project

```bash
mvn clean install
```

### Running the Application

```bash
mvn spring-boot:run
```
Or run the below file:

```bash
com.santander.trivia.TriviaGameApplication
```

## Endpoints

### 1. `POST /trivia/start`

Starts a new trivia game by fetching a random trivia question.

**Response Example:**

```json
{
  "triviaId": 1,
  "question": "Which soccer team won the Copa America 2015 Championship?",
  "possibleAnswers": ["Chile", "Argentina", "Brazil", "Paraguay"]
}
```

### 2. `PUT /trivia/reply/{triviaId}`

Submits an answer to the trivia question.

**Request Body Example:**
```json
{
"answer": "Chile"
}
```
**Response Body Example:**
```json
{
  "result": "Right!"
}
```


## Running Tests

### Unit Tests, Integration Tests, BDD Tests
```bash
mvn test
```
