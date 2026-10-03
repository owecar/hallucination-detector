package com.example.hallucinationdetector.model;

public class Claim {

    private final String text;
    private final String label;
    private final String bestSource;
    private final double score;
    private final boolean hallucination;

    public Claim(String text, String label, String bestSource,
                 double score, boolean hallucination) {
        this.text = text;
        this.label = label;
        this.bestSource = bestSource;
        this.score = score;
        this.hallucination = hallucination;
    }

    public String getText() { return text; }
    public String getLabel() { return label; }
    public String getBestSource() { return bestSource; }
    public double getScore() { return score; }
    public boolean isHallucination() { return hallucination; }
}
