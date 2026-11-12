package com.example.hallucinationdetector.verification;

import com.example.hallucinationdetector.model.ClaimResult;
import com.example.hallucinationdetector.model.SavedQuery;
import com.example.hallucinationdetector.repository.ClaimResultRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/** Saves individual claim details independently of the parent question. */
@Service
public class ClaimHistoryPersistenceService {
    private final ClaimResultRepository results;

    public ClaimHistoryPersistenceService(ClaimResultRepository results) {
        this.results = results;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveClaims(SavedQuery query, List<VerifiedClaim> claims) {
        if (claims == null || claims.isEmpty()) return;
        for (VerifiedClaim claim : claims) {
            results.save(new ClaimResult(query,
                    claim.getText() == null ? "" : claim.getText(),
                    claim.getLabel() == null ? "Unresolved" : claim.getLabel(),
                    claim.getSource(), claim.getExplanation()));
        }
        // Force database errors to surface here, inside this separate transaction.
        results.flush();
    }
}
