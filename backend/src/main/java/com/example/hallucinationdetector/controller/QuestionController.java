package com.example.hallucinationdetector.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hallucinationdetector.claim.ClaimExtractor;
import com.example.hallucinationdetector.detector.HallucinationDetector;
import com.example.hallucinationdetector.factcheck.FactChecker;
import com.example.hallucinationdetector.factcheck.VerificationResult;
import com.example.hallucinationdetector.llm.LLMClient;
import com.example.hallucinationdetector.model.CheckResponse;
import com.example.hallucinationdetector.model.Claim;

@RestController
@RequestMapping("/api")
@CrossOrigin // lets a separate frontend call this; tighten the origin later
public class QuestionController {

    private final LLMClient llm = new LLMClient();
    private final ClaimExtractor extractor = new ClaimExtractor();
    private final FactChecker checker = new FactChecker();
    private final HallucinationDetector detector = new HallucinationDetector();

    // POST /api/check   body: {"question": "Who wrote Hamlet?"}
    @PostMapping("/check")
    public ResponseEntity<CheckResponse> check(@RequestBody Map<String, String> body) {

        String question = body.get("question");
        if (question == null || question.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        String aiAnswer = llm.askLLM(question);
        List<String> claimTexts = extractor.extractClaims(aiAnswer);

        List<Claim> claims = new ArrayList<>();
        for (String claimText : claimTexts) {
            // TODO: replace with real retrieved passages once retrieval is built
            List<String> sources = List.of("... placeholder for trusted sources to check ...");

            VerificationResult result = checker.verifyClaim(claimText, sources);

            claims.add(new Claim(
                    claimText,
                    result.label(),
                    result.bestSource,
                    result.score,
                    detector.isHallucination(result)));
        }

        return ResponseEntity.ok(new CheckResponse(question, aiAnswer, claims));
    }
}
