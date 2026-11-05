package com.example.hallucinationdetector.controller;

import com.example.hallucinationdetector.repository.SavedQueryRepository;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HistoryController {

    private final SavedQueryRepository repository;

    public HistoryController(SavedQueryRepository repository) {
        this.repository = repository;
    }

    // View All Database Records
    @GetMapping("/dbadmin/database")
    public String viewDatabase() {
        return "Html/dbadmin_databaseview";
    }

    // Update/Delete Database Records
    @GetMapping("/dbadmin/update")
    public String updateDatabase() {
        return "Html/dbadmin_updateview";
    }

    // View Question History
    @GetMapping("/history")
    public String viewHistory(Model model) {

        model.addAttribute(
            "history",
            repository.findAll(
                Sort.by(Sort.Direction.DESC, "createdAt")
            )
        );

        return "Html/userhistory";
    }

    // Delete Question From History
    @PostMapping("/history/delete/{id}")
    public String deleteQuestion(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        if (repository.existsById(id)) {

            repository.deleteById(id);

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
}
