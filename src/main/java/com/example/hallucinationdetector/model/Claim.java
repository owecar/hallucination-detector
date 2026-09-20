package com.example.hallucinationdetector.model;

public class Claim {
    private String text; // It will check the text
    private String label; // Labels as supported, unsupported or contradicted
    private String source; // source document that backs up or contradicts the claim

    public Claim(String text, String label, String source){
        this.text = text;
        this.label = label;
        this.source = source;

    }


    
}
