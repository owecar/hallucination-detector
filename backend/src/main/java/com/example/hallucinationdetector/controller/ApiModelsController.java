
package com.example.hallucinationdetector.controller;

import com.example.hallucinationdetector.llm.LLMClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApiModelsController {

    private final LLMClient llmClient;

    public ApiModelsController(LLMClient llmClient) {
        this.llmClient = llmClient;
    }

    @GetMapping("/admin/api-models")
    public String showApiModels(Model model) {
        model.addAttribute("provider", "Google Gemini");
        model.addAttribute("activeModel", llmClient.getActiveModel());
        model.addAttribute("keyConfigured", llmClient.isApiKeyConfigured());
        model.addAttribute("connectionStatus", "Not tested");

        return "Html/api_models";
    }
}
