package com.example.hallucinationdetector.controller;

import com.example.hallucinationdetector.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.hallucinationdetector.model.AppUser;
import org.springframework.web.bind.annotation.GetMapping;

@Controller 
public class AuthController {
    
    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AppUserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String showLogin() {
        return "Html/login";
    }

    @GetMapping("/register")
    public String showRegistration() {
        return "Html/register";
    }

    @GetMapping("/dbadmin")
    public String showDatabaseAdmin() {
        return "Html/dbadmin_mainview";
    }    

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password, @RequestParam String confirmPassword, @RequestParam String role, Model model) {

        if (username.isBlank()) {
            model.addAttribute("error", "Please enter a username");
            return "Html/register";
        }

        if (password.length() < 8) {
            model.addAttribute("error", "Password must contain at least 8 characters.");
            return "Html/register";
        }

        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "Html/register";
        }

        if (!role.equals("USER")
            && !role.equals("ADMIN")
            && !role.equals("DB_ADMIN")) {
        model.addAttribute("error", "Please choose a valid account type.");
        return "Html/register";
        }

        if (repository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "That username is already taken.");
            return "Html/register";
        }

        String passwordHash = passwordEncoder.encode(password);

        AppUser user = new AppUser(username, passwordHash, role);

        repository.save(user);

        return "redirect:/login";
    }
}
