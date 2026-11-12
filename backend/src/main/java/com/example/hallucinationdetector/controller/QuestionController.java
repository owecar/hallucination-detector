package com.example.hallucinationdetector.controller;

import com.example.hallucinationdetector.model.ClaimResult;
import com.example.hallucinationdetector.repository.ClaimResultRepository;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.hallucinationdetector.claim.ClaimExtractor;
import com.example.hallucinationdetector.llm.LLMClient;
import com.example.hallucinationdetector.model.AppUser;
import com.example.hallucinationdetector.model.SavedQuery;
import com.example.hallucinationdetector.repository.AppUserRepository;
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
    private final AppUserRepository userRepository;
    private final ClaimResultRepository claimResultRepository;
    private final com.example.hallucinationdetector.verification.VerificationHistoryService historyService;

   
public QuestionController(
        LLMClient llm,
        ClaimExtractor extractor,
        SavedQueryRepository repository,
        ClaimVerifier verifier,
        AppUserRepository userRepository,
        ClaimResultRepository claimResultRepository,
        com.example.hallucinationdetector.verification.VerificationHistoryService historyService) {

    this.llm = llm;
    this.extractor = extractor;
    this.repository = repository;
    this.verifier = verifier;
    this.userRepository = userRepository;
    this.claimResultRepository = claimResultRepository;
    this.historyService = historyService;
}


    @GetMapping("/")
    public String showForm() {
        return "Html/index";
    }

    @PostMapping("/submit")
    public String submit(
            @RequestParam("question") String question,
            Model model,
            Authentication authentication) {

        model.addAttribute("question", question);

        if (question == null || question.isBlank()) {
            model.addAttribute("answer", "Please enter a question.");
            model.addAttribute("claims", List.of());
            model.addAttribute("summary",
                    VerificationSummary.from(List.of()));

            return "Html/results";
        }

        String answer = llm.askLLM("""
        Answer in one to five short, standalone factual sentences.
        Put one claim in each sentence. Name the subject explicitly.
        Use plain text without headings, lists, examples, or code blocks.

        Question: %s
        """.formatted(question));

        model.addAttribute("answer", answer);

        if (answer == null || answer.isBlank()
                || answer.startsWith("Error:")) {

            model.addAttribute("claims", List.of());
            model.addAttribute("summary",
                    VerificationSummary.from(List.of()));

            return "Html/results";
        }

        List<VerifiedClaim> claims =
                verifier.verify(question, extractor.extractClaims(answer));

        model.addAttribute("claims", claims);
        model.addAttribute("summary",
                VerificationSummary.from(claims));

        if (authentication == null
                || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Login required");
        }

        AppUser user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow(() ->
                        new AccessDeniedException("User not found"));

        
try {
            boolean detailsSaved = historyService.save(question, answer, user, claims);
            if (!detailsSaved) {
                model.addAttribute("notice", "Question and scores saved to history, but claim details could not be saved. Check the server log.");
            }
        } catch (RuntimeException ex) {
            org.slf4j.LoggerFactory.getLogger(QuestionController.class)
                    .error("Failed to save question to user history", ex);
            model.addAttribute("notice", "Verification completed, but the question could not be saved to history. Check the server log.");
        }

        return "Html/results";
    }
}
