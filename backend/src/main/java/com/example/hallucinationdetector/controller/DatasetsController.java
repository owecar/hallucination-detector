package com.example.hallucinationdetector.controller;

import java.io.IOException;
import java.io.UncheckedIOException;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DatasetsController {
    private final ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/admin/datasets")
    public String showDatasets(Model model) {
        Path sampleFile = Path.of("..", "database", "FEVER", "fever_sample_20.jsonl");

        try (var reader = Files.newBufferedReader(sampleFile)) {
            int supports = 0;
            int refutes = 0;
            int notEnoughInfo = 0;

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                String label = mapper.readTree(line).path("label").asText("");
                switch (label) {
                    case "SUPPORTS" -> supports++;
                    case "REFUTES" -> refutes++;
                    case "NOT ENOUGH INFO" -> notEnoughInfo++;
                    default -> throw new IOException("Invalid FEVER label");
                }
            }

            int entries = supports + refutes + notEnoughInfo;
            model.addAttribute("feverStatus", "Available · " + entries + " entries");
            model.addAttribute("feverLabels", supports + " Supports · "
                    + refutes + " Refutes · " + notEnoughInfo + " Not Enough Info");
        } catch (IOException | JacksonException e) {
            model.addAttribute("feverStatus", "Dataset unavailable");
        }

        return "Html/datasets";
    }
}