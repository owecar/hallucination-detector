
package com.example.hallucinationdetector.verification;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class WikipediaClient {

    private static final Set<String> COMMON_WORDS = Set.of(
            "a", "an", "the", "of", "to", "in", "on", "for",
            "is", "are", "was", "were", "be", "and", "or");

    public static class Passage {
        private final String title;
        private final String text;
        private final String url;
        private final int rank;

        public Passage(String title, String text, String url, int rank) {
            this.title = title;
            this.text = text;
            this.url = url;
            this.rank = rank;
        }

        public String getTitle() {
            return title;
        }

        public String getText() {
            return text;
        }

        public String getUrl() {
            return url;
        }

        public int getRank() {
            return rank;
        }
    }

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

    public List<Passage> search(String query) {
        List<Passage> passages = new ArrayList<>();

        if (query == null || query.isBlank()) {
            return passages;
        }

        try {
            String searchQuery = simplifyQuery(query);

            String url = "https://en.wikipedia.org/w/api.php"
                    + "?action=query"
                    + "&format=json"
                    + "&generator=search"
                    + "&gsrnamespace=0"
                    + "&gsrlimit=5"
                    + "&gsrsearch=" + encode(searchQuery)
                    + "&prop=extracts"
                    + "&exintro=1"
                    + "&explaintext=1"
                    + "&exchars=1200"
                    + "&exlimit=5";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(15))
                    .header("User-Agent",
                            "HallucinationDetector/1.0 (academic project)")
                    .GET()
                    .build();

            HttpResponse<String> response = http.send(
                    request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println(
                        "Wikipedia returned HTTP " + response.statusCode());
                return passages;
            }

            JsonNode root = mapper.readTree(response.body());
            JsonNode pages = root.path("query").path("pages");

            if (!pages.isObject()) {
                return passages;
            }

            var iterator = pages.properties().iterator();

            while (iterator.hasNext()) {
                JsonNode page = iterator.next().getValue();

                String title = page.path("title").asText("");
                String text = page.path("extract").asText("");
                int rank = page.path("index").asInt(999);

                if (title.isBlank() || text.isBlank()) {
                    continue;
                }

                passages.add(new Passage(
                        title,
                        text.trim(),
                        makeUrl(title),
                        rank));
            }

            passages.sort(
                    Comparator.comparingInt(Passage::getRank));

        } catch (Exception e) {
            System.err.println(
                    "Wikipedia retrieval failed: " + e.getMessage());
        }

        return passages;
    }

    private String simplifyQuery(String query) {
        String q = query.trim();

        String lower = q.toLowerCase();

        if (lower.contains("tcp") ||
                lower.contains("transmission control protocol")) {
            return "Transmission Control Protocol";
        }

        if (lower.contains("udp") ||
                lower.contains("user datagram protocol")) {
            return "User Datagram Protocol";
        }

        if (lower.contains("python")) {
            return "Python programming language";
        }

        if (lower.contains("java")) {
            return "Java programming language";
        }

        if (lower.contains("linux")) {
            return "Linux operating system";
        }

        if (lower.contains("http")) {
            return "Hypertext Transfer Protocol";
        }

        String[] words = q.replaceAll("[^a-zA-Z0-9]+", " ").trim().split("\\s+");
        List<String> searchWords = new ArrayList<>();

        for (String word : words) {
            if (word.isEmpty() || COMMON_WORDS.contains(word.toLowerCase(Locale.ROOT))) {
                continue;
            }

            searchWords.add(word);
            if (searchWords.size() == 8) {
                break;
            }
        }

        return searchWords.isEmpty() ? q : String.join(" ", searchWords);
    }

    private static String makeUrl(String title) {
        return "https://en.wikipedia.org/wiki/"
                + encode(title.replace(' ', '_'))
                        .replace("+", "%20");
    }

    private static String encode(String value) {
        return URLEncoder.encode(
                value,
                StandardCharsets.UTF_8);
    }
}
