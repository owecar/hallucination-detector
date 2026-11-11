
package com.example.hallucinationdetector.controller;

import com.example.hallucinationdetector.model.SavedQuery;
import com.example.hallucinationdetector.repository.SavedQueryRepository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DatabaseAdminController {

    private final SavedQueryRepository repository;

    public DatabaseAdminController(SavedQueryRepository repository) {
        this.repository = repository;
    }
  
    @GetMapping("/dbadmin/database")
    public String viewDatabase(Model model) {

        List<SavedQuery> records = getAllDatabaseRecords();

        model.addAttribute("records", records);

        return "Html/dbadmin_databaseview";
    }

    public List<SavedQuery> getAllDatabaseRecords() {

        return repository.findAll(
            Sort.by(Sort.Direction.DESC, "createdAt")
        );
    }
}
