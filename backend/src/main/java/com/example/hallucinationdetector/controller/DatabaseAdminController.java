
package com.example.hallucinationdetector.controller;

import com.example.hallucinationdetector.model.AppUser;
import com.example.hallucinationdetector.model.SavedQuery;
import com.example.hallucinationdetector.repository.AppUserRepository;
import com.example.hallucinationdetector.repository.SavedQueryRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
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

    public DatabaseAdminController(SavedQueryRepository savedQueryRepository,
                                   AppUserRepository appUserRepository) {
        this.savedQueryRepository = savedQueryRepository;
        this.appUserRepository = appUserRepository;
    }

    @GetMapping("/dbadmin/database")
    public String viewDatabase(@RequestParam(defaultValue = "saved_query") String table,
                               Model model) {

        if (!table.equals("saved_query") && !table.equals("app_user")) {
            table = "saved_query";
            model.addAttribute("notice", "Unsupported table selected; showing saved_query.");
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
        return savedQueryRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public List<AppUser> getAllUserRecords() {
        return appUserRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @GetMapping("/dbadmin/update")
    public String updateDatabase(Model model) {

        List<SavedQuery> savedQueries = getAllDatabaseRecords();
        List<AppUser> users = getAllUserRecords();

        model.addAttribute("queries", savedQueries);
        model.addAttribute("users", users);

        return "Html/dbadmin_updateview";
    }

    @PostMapping("/dbadmin/update/saved_query/{id}")
    public String updateSavedQuery(
            @PathVariable Long id,
            @RequestParam String question,
            @RequestParam String answer,
            RedirectAttributes redirectAttributes) {

        SavedQuery query = savedQueryRepository.findById(id).orElse(null);

        if (query == null) {
            redirectAttributes.addFlashAttribute(
                    "error", "Saved query not found.");
            return "redirect:/dbadmin/update";
        }

        if (question == null || question.isBlank()) {
            redirectAttributes.addFlashAttribute(
                    "error", "Question cannot be empty.");
            return "redirect:/dbadmin/update";
        }

        query.setQuestion(question.trim());
        query.setAnswer(answer);

        savedQueryRepository.save(query);

        redirectAttributes.addFlashAttribute(
                "success", "Saved query updated successfully.");

        return "redirect:/dbadmin/update";
    }

    @PostMapping("/dbadmin/delete/saved_query/{id}")
    public String deleteSavedQuery(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        if (!savedQueryRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute(
                    "error", "Saved query not found.");
            return "redirect:/dbadmin/update";
        }

        savedQueryRepository.deleteById(id);

        redirectAttributes.addFlashAttribute(
                "success", "Saved query deleted successfully.");

        return "redirect:/dbadmin/update";
    }
}
