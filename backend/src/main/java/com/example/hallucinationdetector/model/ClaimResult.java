
package com.example.hallucinationdetector.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "claim_result")
public class ClaimResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "saved_query_id", nullable = false)
    private SavedQuery savedQuery;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String claimText;

    @Column(nullable = false)
    private String verdict;

    @Column(columnDefinition = "TEXT")
    private String evidenceSource;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    private LocalDateTime createdAt;

    public ClaimResult() {
    }

    public ClaimResult(
            SavedQuery savedQuery,
            String claimText,
            String verdict,
            String evidenceSource,
            String explanation) {

        this.savedQuery = savedQuery;
        this.claimText = claimText;
        this.verdict = verdict;
        this.evidenceSource = evidenceSource;
        this.explanation = explanation;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public SavedQuery getSavedQuery() {
        return savedQuery;
    }

    public String getClaimText() {
        return claimText;
    }

    public String getVerdict() {
        return verdict;
    }

    public String getEvidenceSource() {
        return evidenceSource;
    }

    public String getExplanation() {
        return explanation;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
