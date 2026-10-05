package com.example.hallucinationdetector.factcheck;

public class VerificationResult {
    public final String bestSource;
    public final double score;
    private final String label;

    public VerificationResult(String bestSource, double score, String label) {
        this.bestSource = bestSource;
        this.score = score;
        this.label = label;
    }

    public String label() { return label; }
}