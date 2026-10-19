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
import com.example.hallucinationdetector.verification.TopicValidator;
import com.example.hallucinationdetector.verification.VerificationSummary;
import com.example.hallucinationdetector.verification.VerifiedClaim;
 
@Controller
public class VerificationController {
    private final LLMClient llm;
    private final ClaimExtractor extractor;
    private final TopicValidator topicValidator;
    private final ClaimVerifier verifier;
    private final SavedQueryRepository repository;
 
    public VerificationController(LLMClient llm, ClaimExtractor extractor, TopicValidator topicValidator, ClaimVerifier verifier, SavedQueryRepository repository) {
        this.llm = llm;
        this.extractor = extractor;
        this.topicValidator = topicValidator;
        this.verifier = verifier;
        this.repository = repository;
    }
    @GetMapping("/verify")
    public String form() {
        return "verify";
    }
    @PostMapping("/verify")
    public String verify(@RequestParam("question") String question, Model model) {
        if (question == null || question.isBlank()) {
            model.addAttribute("error", "Please type a question.");
            return "verify";
        }
        String problem = topicValidator.check(question);
        if (problem != null) {
            model.addAttribute("error", problem);
            return "verify";
        }
        String answer = llm.askLLM(question);
        List<VerifiedClaim> claims = new java.util.ArrayList<>();
        if (answer != null && !answer.startsWith("Error:")) {
            claims = verifier.verify(extractor.extractClaims(answer));
            try {
                repository.save(new SavedQuery(question, answer));
            } catch (Exception e) {
                model.addAttribute("notice", "The result could not be saved to the database.");
            }
        }
        model.addAttribute("question", question);
        model.addAttribute("answer", answer);
        model.addAttribute("claims", claims);
        model.addAttribute("summary", VerificationSummary.from(claims));
        return "verify-results";
    }
}
