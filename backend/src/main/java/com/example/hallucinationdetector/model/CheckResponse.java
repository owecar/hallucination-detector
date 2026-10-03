package com.example.hallucinationdetector.model;

import java.util.List;

public class CheckResponse {

    private final String question;
    private final String answer;
    private final List<Claim> claims;

    public CheckResponse(String question, String answer, List<Claim> claims) {
        this.question = question;
        this.answer = answer;
        this.claims = claims;
    }

    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
    public List<Claim> getClaims() { return claims; }
}
