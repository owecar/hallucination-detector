
package com.example.hallucinationdetector.repository;

import com.example.hallucinationdetector.model.ClaimResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClaimResultRepository
        extends JpaRepository<ClaimResult, Long> {

    long countByVerdict(String verdict);

    @Modifying
    @Query("delete from ClaimResult result where result.savedQuery.id = :questionId")
    void deleteForQuestion(@Param("questionId") Long questionId);
}
