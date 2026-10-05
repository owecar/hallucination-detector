package com.example.hallucinationdetector.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

    @GetMapping("/")
    public String showForm() {
        return "index";
    }
    @PostMapping("/submit")
    public String submitForm(@RequestParam("question") String question, Model model) {
        model.addAttribute("question", question);
        return "results";
    }
}