package com.example.hallucinationdetector.verification;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WikipediaClient {
  public static class Passage {
    private final String title, text, url;
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

  public List<Passage> search(String query) {
    if (query == null || query.isBlank()) {
      return new ArrayList<>();
    }
    try {
      String url = "https://en.wikipedia.org/w/api.php?action=query&format=json"
          + "&generator=search&gsrnamespace=0&gsrlimit=3&gsrsearch=" + encode(query)
          + "&prop=extracts&exintro=1&explaintext=1&exlimit=3";
      HttpRequest request = HttpRequest.newBuilder(URI.create(url))
          .timeout(Duration.ofSeconds(10))
          .header("User-Agent", "HallucinationDetector/1.0 (project)")
          .GET()
          .build();
      HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() != 200) {
        return new ArrayList<>();
      }
      return parsePassages(response.body());
    } catch (Exception e) {
      return new ArrayList<>();
    }
  }

  static List<Passage> parsePassages(String json) {
    List<Passage> list = new ArrayList<>();
    String[] pieces = json.split("\"pageid\":");
    for (int i = 1; i < pieces.length; i++) {
      String piece = pieces[i];
      String title = readText(piece, "title");
      String text = readText(piece, "extract");
      int rank = readNumber(piece, "index");
      if (title.isEmpty() || text.isBlank()) {
        continue;
      }
      list.add(new Passage(title, text.trim(), makeUrl(title), rank));
    }
    list.sort((a, b) -> Integer.compare(a.getRank(), b.getRank()));
    return list;
  }

  static String readText(String piece, String key) {
    String marker = "\"" + key + "\":\"";
    int start = piece.indexOf(marker);
    if (start < 0) {
      return "";
    }
    start += marker.length();
    int end = start;
    while (end < piece.length()) {
      char c = piece.charAt(end);
      if (c == '\\') {
        end += 2;
      } else if (c == '"') {
        break;
      } else {
        end++;
      }
    }
    if (end > piece.length()) {
      end = piece.length();
    }
    return unescape(piece.substring(start, end));
  }

  static int readNumber(String piece, String key) {
    String marker = "\"" + key + "\":";
    int start = piece.indexOf(marker);
    if (start < 0) {
      return 999;
    }
    start += marker.length();
    int end = start;
    while (end < piece.length() && Character.isDigit(piece.charAt(end))) {
      end++;
    }
    if (end == start) {
      return 999;
    }
    return Integer.parseInt(piece.substring(start, end));
  }

  static String unescape(String s) {
    StringBuilder result = new StringBuilder();

    for (int i = 0; i < s.length(); i++) {
      char current = s.charAt(i);
      if (current != '\\' || i + 1 >= s.length()) {
        result.append(current);
        continue;
      }

      char next = s.charAt(++i);
      if (next == 'n' || next == 't' || next == 'r') {
        result.append(' ');
      } else if (next == 'u' && i + 4 < s.length()) {
        try {
          result.append((char) Integer.parseInt(s.substring(i + 1, i + 5), 16));
          i += 4;
        } catch (NumberFormatException e) {
          result.append("\\u");
        }
      } else {
        result.append(next);
      }
    }

    return result.toString();
  }

  static String makeUrl(String title) {
    return "https://en.wikipedia.org/wiki/" + encode(title.replace(' ', '_'));
  }

  private static String encode(String s) {
    return URLEncoder.encode(s, StandardCharsets.UTF_8);
  }
}
