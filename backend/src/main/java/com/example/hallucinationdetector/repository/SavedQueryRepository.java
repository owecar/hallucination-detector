package com.example.hallucinationdetector.repository;

import com.example.hallucinationdetector.model.SavedQuery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SavedQueryRepository
        extends JpaRepository<SavedQuery, Long> {
}