package com.example.hallucinationdetector.controller;

import com.example.hallucinationdetector.llm.LLMClient;
import com.example.hallucinationdetector.llm.ApiKeyService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApiModelsController {

    private final LLMClient llmClient;
    private final ApiKeyService apiKeyService;

    public ApiModelsController(LLMClient llmClient, ApiKeyService apiKeyService) {
        this.llmClient = llmClient;
        this.apiKeyService = apiKeyService;
    }

    @GetMapping("/admin/api-models")
    public String showApiModels(Model model) {
        model.addAttribute("provider", "Google Gemini");
        model.addAttribute("activeModel", llmClient.getActiveModel());
        model.addAttribute("keyConfigured", llmClient.isApiKeyConfigured());
        model.addAttribute("connectionStatus", "Not tested");
        model.addAttribute("temporaryKeyConfigured", apiKeyService.hasTemporaryKey());

        return "Html/api_models";
    }
    @PostMapping("/admin/api-models/key")
    public String saveApiKey(@RequestParam("apiKey") String apiKey,
                             RedirectAttributes flash) {
        if (apiKey == null || apiKey.isBlank()) {
            flash.addFlashAttribute("keyError", "Please enter an API key.");
        } else {
            apiKeyService.setKey(apiKey);
            flash.addFlashAttribute("keySuccess", "Temporary API key saved. It will reset when the application restarts.");
        }
        return "redirect:/admin/api-models";
    }

    @PostMapping("/admin/api-models/key/clear")
    public String clearApiKey(RedirectAttributes flash) {
        apiKeyService.clearKey();
        flash.addFlashAttribute("keySuccess", "Temporary API key cleared.");
        return "redirect:/admin/api-models";
    }
}
