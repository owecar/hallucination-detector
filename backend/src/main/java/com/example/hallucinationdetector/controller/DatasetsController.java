package com.example.hallucinationdetector.controller;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DatasetsController {

    @GetMapping("/admin/datasets")
    public String showDatasets(Model model) {
        Path sampleFile = Path.of("..", "database", "FEVER", "fever_sample_20.jsonl");

        try (var lines = Files.lines(sampleFile)) {
            long entries = lines.filter(line -> !line.isBlank()).count();
            model.addAttribute("feverStatus", "Available · " + entries + " entries");
        } catch (IOException | UncheckedIOException e) {
            model.addAttribute("feverStatus", "Dataset unavailable");
        }

        return "Html/datasets";
    }
}