package com.example.hallucinationdetector.claim;
//This is just a test to have the program compile
import java.util.ArrayList;
import java.util.List;

public class ClaimExtractor {

    public List<String> extractClaims(String answer) {
        List<String> claims = new ArrayList<>();

        if (answer == null || answer.isBlank()) {
            return claims;
        }

        claims.add(answer);

        return claims;
    }
}