
package com.example.hallucinationdetector.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DatasetsController {

    @GetMapping("/admin/datasets")
    public String showDatasets() {
        return "Html/datasets";
    }
}
