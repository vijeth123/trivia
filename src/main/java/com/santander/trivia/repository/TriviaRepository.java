package com.santander.trivia.repository;

import com.santander.trivia.model.Trivia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TriviaRepository extends JpaRepository<Trivia, Long> {
}