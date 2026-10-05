package com.example.hallucinationdetector.llm;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class LLMClient {
    private final String apiKey = System.getenv("GEMINI_API_KEY");
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/openai/chat/completions";
    private static final Pattern CONTENT_PATTERN =
            Pattern.compile("\"content\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");
    public String askLLM(String prompt) {
        try {
            String escapedPrompt = prompt.replace("\\", "\\\\").replace("\"", "\\\"");
            String requestBody = "{\"model\":\"gemini-2.5-flash\",\"messages\":[{\"role\":\"user\",\"content\":\"" + escapedPrompt + "\"}]}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return "Error: the AI service returned status " + response.statusCode();
            }

            Matcher matcher = CONTENT_PATTERN.matcher(response.body());
            if (matcher.find()) {
                return matcher.group(1)
                        .replace("\\n", "\n")
                        .replace("\\\"", "\"")
                        .replace("\\\\", "\\");
            }
            return "Error: could not parse the AI response.";
        } catch (Exception e) {
            return "Error: could not reach the AI model.";
        }
    }
}
