package com.example.hallucinationdetector.claim;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class ClaimExtractor {

    // Splits an AI answer into individual sentences, one claim per sentence.
    public List<String> extractClaims(String answer) {
        List<String> claims = new ArrayList<>();
        if (answer == null || answer.isBlank()) {
            return claims;
        }

        for (String sentence : answer.split("(?<=[.!?])\\s+")) {
            if (!sentence.isBlank()) {
                claims.add(sentence.trim());
            }
        }
        return claims;
    }
}
