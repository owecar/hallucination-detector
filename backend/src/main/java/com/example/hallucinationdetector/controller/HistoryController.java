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

@Controller
public class HistoryController {

    private final SavedQueryRepository repository;
    private final AppUserRepository userRepository;

    public HistoryController(SavedQueryRepository repository, AppUserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @GetMapping("/history")
    public String viewHistory(Model model, Authentication authentication) {
        AppUser user = currentUser(authentication);

        model.addAttribute(
            "history",
            repository.findByOwner_IdOrderByCreatedAtDesc(user.getId())
        );

        return "Html/userhistory";
    }

    // Delete Question From History
    @PostMapping("/history/delete/{id}")
    public String deleteQuestion(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {
        AppUser user = currentUser(authentication);
        var record = repository.findByIdAndOwner_Id(id, user.getId());

        if (record.isPresent()) {

            repository.delete(record.get());

            redirectAttributes.addFlashAttribute(
                "message",
                "Question deleted successfully."
            );

        } else {

            redirectAttributes.addFlashAttribute(
                "message",
                "Question not found."
            );
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
