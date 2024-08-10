package com.santander.trivia.model;


import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Trivia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long triviaId;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private String correctAnswer;

    @Column(nullable = false)
    private int answerAttempts = 0;
}
