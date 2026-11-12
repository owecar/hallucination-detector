package com.example.hallucinationdetector.controller;

import com.example.hallucinationdetector.repository.SavedQueryRepository;
import com.example.hallucinationdetector.repository.AppUserRepository;
import com.example.hallucinationdetector.model.AppUser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.hallucinationdetector.repository.ClaimResultRepository;
import org.springframework.transaction.annotation.Transactional;

@Controller
public class HistoryController {

    private final SavedQueryRepository repository;
    private final AppUserRepository userRepository;
    private final ClaimResultRepository claimResultRepository;

    public HistoryController(SavedQueryRepository repository,
            AppUserRepository userRepository,
            ClaimResultRepository claimResultRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.claimResultRepository = claimResultRepository;
    }

    @GetMapping("/history")
    public String viewHistory(Model model, Authentication authentication) {
        AppUser user = currentUser(authentication);

        model.addAttribute(
                "history",
                repository.findByOwner_IdOrderByCreatedAtDesc(user.getId()));

        return "Html/userhistory";
    }

    @Transactional 
    @PostMapping("/history/delete/{id}")
    public String deleteQuestion(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {
        AppUser user = currentUser(authentication);
        var record = repository.findByIdAndOwner_Id(id, user.getId());

        if (record.isPresent()) {

            claimResultRepository.deleteForQuestion(id);
            repository.delete(record.get());

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Question deleted successfully.");

        } else {

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Question not found.");
        }

        return "redirect:/history";
    }

    private AppUser currentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new org.springframework.security.access.AccessDeniedException("Login required");
        }
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("User not found"));
    }
}
