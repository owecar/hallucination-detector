package com.example.hallucinationdetector.factcheck;

import java.util.List;

public class FactChecker {
    public VerificationResult verifyClaim(String claim, List<String> sources) {
        String bestSource = "No source available";
        double bestScore = 0.0;
        for (String source : sources) {
            double score = 0.0; // TODO: real similarity/NLI scoring later
            if (score > bestScore) { bestScore = score; bestSource = source; }
        }
        String label = bestScore >= 0.7 ? "supported" : bestScore >= 0.4 ? "unsupported" : "contradicted";
        return new VerificationResult(bestSource, bestScore, label);
    }
}