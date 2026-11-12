package com.example.hallucinationdetector.repository;

import com.example.hallucinationdetector.model.SavedQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SavedQueryRepository
        extends JpaRepository<SavedQuery, Long> {
    List<SavedQuery> findByOwner_IdOrderByCreatedAtDesc(Long ownerId);
    Optional<SavedQuery> findByIdAndOwner_Id(Long id, Long ownerId);
}
