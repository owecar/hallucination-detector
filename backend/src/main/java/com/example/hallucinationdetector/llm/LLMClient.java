package com.example.hallucinationdetector.llm;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LLMClient {

    // Key comes from the GEMINI_API_KEY environment variable 
    @Value("${gemini.api.key:}")
    private String configuredKey;

    @Value("${gemini.model:gemini-3.8-flash}")
    private String model;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/openai/chat/completions";
    private static final Pattern CONTENT_PATTERN =
            Pattern.compile("\"content\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");
    private static final Pattern MESSAGE_PATTERN =
            Pattern.compile("\"message\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"");
    private String resolveKey() {
        if (configuredKey != null && !configuredKey.isBlank()) {
            return configuredKey.trim();
        }
        String env = System.getenv("GEMINI_API_KEY");
        return env == null ? "" : env.trim();
    }

    public String askLLM(String prompt) {
        String apiKey = resolveKey();
        if (apiKey.isEmpty()) {
            return "Error: no API key found. Set the GEMINI_API_KEY environment variable "
                    + "(see the README), then restart the app.";
        }

        try {
            String escapedPrompt = prompt
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");

            String requestBody = "{\"model\":\"" + model + "\",\"messages\":"
                    + "[{\"role\":\"user\",\"content\":\"" + escapedPrompt + "\"}]}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                String detail = "";
                Matcher err = MESSAGE_PATTERN.matcher(response.body());
                if (err.find()) {
                    detail = " - " + unescape(err.group(1));
                }
                return "Error: the AI service returned status " + response.statusCode() + detail;
            }

            Matcher matcher = CONTENT_PATTERN.matcher(response.body());
            if (matcher.find()) {
                return unescape(matcher.group(1));
            }
            return "Error: could not parse the AI response.";

        } catch (Exception e) {
            return "Error: could not reach the AI model (" + e.getClass().getSimpleName() + ").";
        }
    }

    // Turns JSON escape sequences back into normal text.
    private String unescape(String s) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char n = s.charAt(++i);
                switch (n) {
                    case 'n': out.append('\n'); break;
                    case 'r': out.append('\r'); break;
                    case 't': out.append('\t'); break;
                    case '"': out.append('"'); break;
                    case '\\': out.append('\\'); break;
                    case '/': out.append('/'); break;
                    case 'u':
                        if (i + 4 < s.length()) {
                            out.append((char) Integer.parseInt(s.substring(i + 1, i + 5), 16));
                            i += 4;
                        }
                        break;
                    default: out.append(n);
                }
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }
}
