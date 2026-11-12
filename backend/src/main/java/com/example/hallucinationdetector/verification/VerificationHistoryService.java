package com.example.hallucinationdetector.verification;

import com.example.hallucinationdetector.model.AppUser;
import com.example.hallucinationdetector.model.SavedQuery;
import com.example.hallucinationdetector.repository.SavedQueryRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VerificationHistoryService {
    private static final Logger log = LoggerFactory.getLogger(VerificationHistoryService.class);
    private final SavedQueryRepository queries;
    private final ClaimHistoryPersistenceService claimPersistence;

    public VerificationHistoryService(SavedQueryRepository queries,
            ClaimHistoryPersistenceService claimPersistence) {
        this.queries = queries;
        this.claimPersistence = claimPersistence;
    }

    /** Commit the question and its summary first so a claim-detail error cannot erase it. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public SavedQuery saveQuestion(String question, String answer, AppUser owner,
            List<VerifiedClaim> claims) {
        VerificationSummary summary = VerificationSummary.from(
                claims == null ? List.of() : claims);
        SavedQuery query = new SavedQuery(question, answer, owner);
        query.setHallucinationScore(summary.getHallucinationScore());
        query.setAccuracyScore(summary.getAccuracyScore());
        query.setVerificationCoverage(summary.getVerificationCoverage());
        query.setSupportedClaims(summary.getSupported());
        query.setContradictedClaims(summary.getContradicted());
        query.setUnresolvedClaims(summary.getUnresolved());
        return queries.saveAndFlush(query);
    }

    /** Returns true if all details were saved; false if the question saved but details failed. */
    public boolean save(String question, String answer, AppUser owner,
            List<VerifiedClaim> claims) {
        SavedQuery query = saveQuestion(question, answer, owner, claims);
        try {
            claimPersistence.saveClaims(query, claims);
            return true;
        } catch (RuntimeException ex) {
            log.error("Question {} saved, but individual claim details could not be stored", query.getId(), ex);
            return false;
        }
    }
}
