
package com.example.hallucinationdetector.repository;

import com.example.hallucinationdetector.model.ClaimResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClaimResultRepository
        extends JpaRepository<ClaimResult, Long> {

    long countByVerdict(String verdict);
}
