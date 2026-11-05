package com.example.hallucinationdetector.config;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class AccountAdvice {

    @ModelAttribute
    public void addAccountDetails(Authentication authentication, Model model) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return;
        }

        String role = "User";

        for (var authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_DB_ADMIN")) {
                role = "Database Admin";
                break;
            }

            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                role = "Admin";
            }
        }

        model.addAttribute("signedInUsername", authentication.getName());
        model.addAttribute("signedInRole", role);
    }
}