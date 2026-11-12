package com.example.hallucinationdetector.controller;
import com.example.hallucinationdetector.repository.SavedQueryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.example.hallucinationdetector.repository.ClaimResultRepository;
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
    private final SavedQueryRepository savedQueryRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClaimResultRepository claimResultRepository;
    public AuthController(
        AppUserRepository repository,
        SavedQueryRepository savedQueryRepository,
        ClaimResultRepository claimResultRepository,
        PasswordEncoder passwordEncoder) {

        this.repository = repository;
        this.savedQueryRepository = savedQueryRepository;
        this.claimResultRepository = claimResultRepository;
        this.passwordEncoder = passwordEncoder;
}

@GetMapping("/login")
public String showLogin() {
    return "Html/login";
}

@GetMapping("/register")
public String showRegister() {
    return "Html/register";
}

@GetMapping("/dbadmin")
public String showDatabaseAdmin() {
    return "Html/dbadmin_mainview";
}

@GetMapping("/admin")
public String showAdmin(Model model) {

    // Real database statistics
    model.addAttribute("totalUsers", repository.count());
    model.addAttribute("totalQueries", savedQueryRepository.count());

    // Five most recent questions
    model.addAttribute(
        "recentQuestions",
        savedQueryRepository.findAll(
            PageRequest.of(
                0,
                5,
                Sort.by(Sort.Direction.DESC, "createdAt")
            )
        ).getContent()
    );

    // Real verification counts
    long supported =
            claimResultRepository.countByVerdict("Supported");

    long contradicted =
            claimResultRepository.countByVerdict("Contradicted");

    long totalVerified = supported + contradicted;

    double hallucinationRate = totalVerified > 0
            ? (contradicted * 100.0) / totalVerified
            : 0.0;

    model.addAttribute("supportedClaims", supported);
    model.addAttribute("contradictedClaims", contradicted);
    model.addAttribute("hallucinationRate", hallucinationRate);
    model.addAttribute("hasVerificationData", totalVerified > 0);

    return "Html/admin_mainview";
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
