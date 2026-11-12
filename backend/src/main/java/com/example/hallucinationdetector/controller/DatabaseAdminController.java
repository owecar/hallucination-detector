
package com.example.hallucinationdetector.controller;

import com.example.hallucinationdetector.model.AppUser;
import com.example.hallucinationdetector.model.SavedQuery;
import com.example.hallucinationdetector.repository.AppUserRepository;
import com.example.hallucinationdetector.repository.ClaimResultRepository;
import com.example.hallucinationdetector.repository.SavedQueryRepository;
import java.security.Principal;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DatabaseAdminController {

    private final SavedQueryRepository savedQueryRepository;
    private final AppUserRepository appUserRepository;
    private final ClaimResultRepository claimResultRepository;

    public DatabaseAdminController(
            SavedQueryRepository savedQueryRepository,
            AppUserRepository appUserRepository,
            ClaimResultRepository claimResultRepository) {
        this.savedQueryRepository = savedQueryRepository;
        this.appUserRepository = appUserRepository;
        this.claimResultRepository = claimResultRepository;
    }

    @GetMapping("/dbadmin/database")
    public String viewDatabase(
            @RequestParam(defaultValue = "saved_query") String table,
            Model model) {

        if (!table.equals("saved_query") && !table.equals("app_user")) {
            table = "saved_query";
            model.addAttribute("notice",
                    "Unsupported table selected; showing saved_query.");
        }

        model.addAttribute("selectedTable", table);
        model.addAttribute("savedQueryCount", savedQueryRepository.count());
        model.addAttribute("appUserCount", appUserRepository.count());

        if (table.equals("app_user")) {
            model.addAttribute("users", getAllUserRecords());
        } else {
            model.addAttribute("queries", getAllDatabaseRecords());
        }

        return "Html/dbadmin_databaseview";
    }

    public List<SavedQuery> getAllDatabaseRecords() {
        return savedQueryRepository.findAll(
                Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public List<AppUser> getAllUserRecords() {
        return appUserRepository.findAll(
                Sort.by(Sort.Direction.ASC, "id"));
    }

    @GetMapping("/dbadmin/update")
    public String updateDatabase(Model model) {
        model.addAttribute("queries", getAllDatabaseRecords());
        model.addAttribute("users", getAllUserRecords());

        return "Html/dbadmin_updateview";
    }

    @PostMapping("/dbadmin/update/saved_query/{id}")
    public String updateSavedQuery(
            @PathVariable Long id,
            @RequestParam String question,
            @RequestParam(required = false) String answer,
            RedirectAttributes redirectAttributes) {

        SavedQuery query = savedQueryRepository.findById(id).orElse(null);

        if (query == null) {
            return redirectWithError(
                    redirectAttributes, "Saved query not found.");
        }

        if (question == null || question.isBlank()) {
            return redirectWithError(
                    redirectAttributes, "Question cannot be empty.");
        }

        query.setQuestion(question.trim());
        query.setAnswer(answer);

        savedQueryRepository.save(query);

        return redirectWithSuccess(
                redirectAttributes, "Saved query updated successfully.");
    }

    @Transactional
    @PostMapping("/dbadmin/delete/saved_query/{id}")
    public String deleteSavedQuery(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        if (!savedQueryRepository.existsById(id)) {
            return redirectWithError(
                    redirectAttributes, "Saved query not found.");
        }

        claimResultRepository.deleteForQuestion(id);
        savedQueryRepository.deleteById(id);

        return redirectWithSuccess(
                redirectAttributes, "Saved query deleted successfully.");
    }

    @PostMapping("/dbadmin/update/app_user/{id}")
    public String updateUser(
            @PathVariable Long id,
            @RequestParam String username,
            @RequestParam String role,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        AppUser user = appUserRepository.findById(id).orElse(null);

        if (user == null) {
            return redirectWithError(
                    redirectAttributes, "User not found.");
        }

        if (username == null || username.isBlank()) {
            return redirectWithError(
                    redirectAttributes, "Username cannot be empty.");
        }

        username = username.trim();

        if (!isValidRole(role)) {
            return redirectWithError(
                    redirectAttributes, "Invalid account role.");
        }

        if (principal == null) {
            return redirectWithError(
                    redirectAttributes, "Authentication required.");
        }

        if (user.getUsername().equals(principal.getName())) {
            if (!username.equals(user.getUsername())
                    || !role.equals(user.getRole())) {
                return redirectWithError(
                        redirectAttributes,
                        "You cannot change your own username or role.");
            }
        }

        if (user.getRole().equals("DB_ADMIN")
                && !role.equals("DB_ADMIN")
                && appUserRepository.countByRole("DB_ADMIN") <= 1) {
            return redirectWithError(
                    redirectAttributes,
                    "Cannot remove the last database administrator.");
        }

        boolean usernameTaken = appUserRepository
                .findByUsername(username)
                .filter(existing -> !existing.getId().equals(id))
                .isPresent();

        if (usernameTaken) {
            return redirectWithError(
                    redirectAttributes, "Username already exists.");
        }

        user.setUsername(username);
        user.setRole(role);

        try {
            appUserRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException exception) {
            return redirectWithError(
                    redirectAttributes,
                    "Unable to update user. Username may already exist.");
        }

        return redirectWithSuccess(
                redirectAttributes, "User updated successfully.");
    }

    @PostMapping("/dbadmin/delete/app_user/{id}")
    public String deleteUser(
            @PathVariable Long id,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        AppUser user = appUserRepository.findById(id).orElse(null);

        if (user == null) {
            return redirectWithError(
                    redirectAttributes, "User not found.");
        }

        if (principal == null) {
            return redirectWithError(
                    redirectAttributes, "Authentication required.");
        }

        if (user.getUsername().equals(principal.getName())) {
            return redirectWithError(
                    redirectAttributes,
                    "You cannot delete your own account.");
        }

        if (user.getRole().equals("DB_ADMIN")
                && appUserRepository.countByRole("DB_ADMIN") <= 1) {
            return redirectWithError(
                    redirectAttributes,
                    "Cannot delete the last database administrator.");
        }

        try {
            appUserRepository.delete(user);
            appUserRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            return redirectWithError(
                    redirectAttributes,
                    "Unable to delete user because related records exist.");
        }

        return redirectWithSuccess(
                redirectAttributes, "User deleted successfully.");
    }

    private boolean isValidRole(String role) {
        return "USER".equals(role)
                || "ADMIN".equals(role)
                || "DB_ADMIN".equals(role);
    }

    private String redirectWithError(
            RedirectAttributes redirectAttributes,
            String message) {

        redirectAttributes.addFlashAttribute("error", message);
        return "redirect:/dbadmin/update";
    }

    private String redirectWithSuccess(
            RedirectAttributes redirectAttributes,
            String message) {

        redirectAttributes.addFlashAttribute("success", message);
        return "redirect:/dbadmin/update";
    }
}
