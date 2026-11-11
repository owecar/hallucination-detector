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
import org.springframework.web.bind.annotation.RequestParam;

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
    public String updateDatabase() {
        return "Html/dbadmin_updateview";
    }
}
