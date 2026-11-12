package com.example.hallucinationdetector.llm;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class LLMClient {

    private final ApiKeyService apiKeyService;

    public LLMClient(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @Value("${gemini.api.key:}")
    private String configuredKey;

    @Value("${gemini.model:gemini-3.1-flash-lite}")
    private String model;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/openai/chat/completions";

    private String resolveKey() {
        if (apiKeyService.hasTemporaryKey()) {
            return apiKeyService.getKey();
        }
        if (configuredKey != null && !configuredKey.isBlank()) {
            return configuredKey.trim();
        }

        String env = System.getenv("GEMINI_API_KEY");
        return env == null ? "" : env.trim();
    }

    public String askLLM(String prompt) {
        String apiKey = resolveKey();

        if (apiKey.isEmpty()) {
            return "Error: no API key found. Ask an administrator to configure the Gemini API key under API & Models.";
        }

        try {
            String requestBody = objectMapper.writeValueAsString(
                    Map.of(
                            "model", model,
                            "messages", List.of(
                                    Map.of(
                                            "role", "user",
                                            "content", prompt
                                    )
                            )
                    )
            );

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .timeout(Duration.ofSeconds(60))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            JsonNode root = objectMapper.readTree(response.body());

            if (response.statusCode() != 200) {
                String detail = root.path("error")
                        .path("message")
                        .asText("");

                return "Error: the AI service returned status "
                        + response.statusCode()
                        + (detail.isBlank() ? "" : " - " + detail);
            }

            JsonNode choices = root.path("choices");

            if (!choices.isArray() || choices.isEmpty()) {
                return "Error: the AI response contained no choices.";
            }

            String content = choices.get(0)
                    .path("message")
                    .path("content")
                    .asText("");

            if (content.isBlank()) {
                return "Error: the AI response was empty.";
            }

            return content;

        } catch (Exception e) {
            return "Error: could not reach the AI model ("
                    + e.getClass().getSimpleName() + ").";
        }
    }

public String getActiveModel() {
    return model;
}

public boolean isApiKeyConfigured() {
    return !resolveKey().isEmpty();
}}
