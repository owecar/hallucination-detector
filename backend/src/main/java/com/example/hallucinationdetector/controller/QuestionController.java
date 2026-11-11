package com.example.hallucinationdetector.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.hallucinationdetector.claim.ClaimExtractor;
import com.example.hallucinationdetector.llm.LLMClient;
import com.example.hallucinationdetector.model.SavedQuery;
import com.example.hallucinationdetector.repository.SavedQueryRepository;
import com.example.hallucinationdetector.verification.ClaimVerifier;
import com.example.hallucinationdetector.verification.VerificationSummary;
import com.example.hallucinationdetector.verification.VerifiedClaim;

@Controller
public class QuestionController {

    private final LLMClient llm;
    private final ClaimExtractor extractor;
    private final SavedQueryRepository repository;
    private final ClaimVerifier verifier;

    public QuestionController(
            LLMClient llm,
            ClaimExtractor extractor,
            SavedQueryRepository repository,
            ClaimVerifier verifier) {

        this.llm = llm;
        this.extractor = extractor;
        this.repository = repository;
        this.verifier = verifier;
    }

    @GetMapping("/")
    public String showForm() {
        return "Html/index";
    }

    @PostMapping("/submit")
    public String submit(
            @RequestParam("question") String question,
            Model model) {

        model.addAttribute("question", question);

        if (question == null || question.isBlank()) {
            model.addAttribute("answer", "Please enter a question.");
            model.addAttribute("claims", List.of());
            model.addAttribute("summary",
                    VerificationSummary.from(List.of()));
            return "Html/results";
        }

        String answer = llm.askLLM(question);

        model.addAttribute("answer", answer);

        if (answer == null || answer.isBlank()
                || answer.startsWith("Error:")) {

            model.addAttribute("claims", List.of());
            model.addAttribute("summary",
                    VerificationSummary.from(List.of()));
            return "Html/results";
        }

        List<VerifiedClaim> claims =
                verifier.verify(extractor.extractClaims(answer));

        model.addAttribute("claims", claims);
        model.addAttribute("summary", VerificationSummary.from(claims));
        model.addAttribute("summary",
                VerificationSummary.from(claims));

        repository.save(new SavedQuery(question, answer));

        return "Html/results";
    }
}
