package com.example.hallucinationdetector.model;

public class Claim {

    private final String text;
    private final String label;
    private final String source;

    public Claim(String text, String label, String source) {
        this.text = text;
        this.label = label;
        this.source = source;
    }

    public String getText() { return text; }
    public String getLabel() { return label; }
    public String getSource() { return source; }
}
