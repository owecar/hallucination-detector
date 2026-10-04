package com.example.hallucinationdetector.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class SavedQuery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String question;

    @Column(length = 2000)
    private String answer;

    private LocalDateTime createdAt = LocalDateTime.now();

    public SavedQuery() {}

    public SavedQuery(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public Long getId() { return id; }
    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}