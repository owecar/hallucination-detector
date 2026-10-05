package com.example.hallucinationdetector.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.hallucinationdetector.claim.ClaimExtractor;
import com.example.hallucinationdetector.model.CheckResponse;
import com.example.hallucinationdetector.model.Claim;
import com.example.hallucinationdetector.model.SavedQuery;
import com.example.hallucinationdetector.repository.SavedQueryRepository;
import com.example.hallucinationdetector.service.LLMClient;

@Controller
public class QuestionController {

    private final LLMClient llm;
    private final ClaimExtractor extractor;
    private final SavedQueryRepository repository;

    public QuestionController(LLMClient llm, ClaimExtractor extractor,
                              SavedQueryRepository repository) {
        this.llm = llm;
        this.extractor = extractor;
        this.repository = repository;
    }

    // GET / -> templates/index.html
    @GetMapping("/")
    public String showForm() {
        return "index";
    }

    // POST /check -> templates/results.html
    @PostMapping("/check")
    public String check(@RequestParam("question") String question, Model model) {

        String answer = llm.askLLM(question);

        // One claim per sentence. Labels stay "Unchecked" until fact checking exists.
        List<Claim> claims = new ArrayList<>();
        for (String text : extractor.extractClaims(answer)) {
            claims.add(new Claim(text, "Unchecked", "N/A"));
        }
        CheckResponse response = new CheckResponse(question, answer, claims);

        repository.save(new SavedQuery(question, answer));

        model.addAttribute("question", response.getQuestion());
        model.addAttribute("answer", response.getAnswer());
        model.addAttribute("claims", response.getClaims());
        return "results";
    }
}
