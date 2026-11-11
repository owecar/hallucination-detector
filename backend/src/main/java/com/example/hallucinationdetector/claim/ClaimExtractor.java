package com.example.hallucinationdetector.claim;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
@Service
public class ClaimExtractor {
    // Smallest sentence (in words) that is worth checking as a claim.
    private static final int MIN_WORDS = 3;
    // Splits an AI answer into individual sentences, one claim per sentence.
    // Gemini often answers with bullet lists, numbered lists and **bold** text, so each line is cleaned first and then split into sentences.
    public List<String> extractClaims(String answer) {
        List<String> claims = new ArrayList<>();
        if (answer == null || answer.isBlank()) {
            return claims;
        }

        for (String line : answer.split("\\R+")) {
            String cleaned = cleanLine(line);

            // An intro line such as "Here are the main points:" is not a claim.
            if (cleaned.isBlank() || cleaned.endsWith(":")) {
                continue;
            }

            for (String sentence : cleaned.split("(?<=[.!?])\\s+")) {
                String claim = sentence.trim();
                if (wordCount(claim) >= MIN_WORDS) {
                    claims.add(claim);
                }
            }
        }
        return claims;
    }
    // Removes list markers (-, *, 1.), markdown symbols (#, **, `) and extra spaces.
    private String cleanLine(String line) {
        String text = line.trim();
        text = text.replaceFirst("^(#+\\s*|[*\\-•]\\s+|\\d+[.)]\\s+)", "");
        text = text.replace("**", "").replace("__", "").replace("`", "");
        return text.trim();
    }
    private int wordCount(String text) {
        if (text.isBlank()) {
            return 0;
        }
        return text.trim().split("\\s+").length;
    }
}
