package com.example.hallucinationdetector.verification;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import jaava.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class WikipediaClient{
  public static class Passage{
    private final String title, text, url;
    private final int rank;

    public Passage(String title, String text, String url, int rank){
      this.title = title;
      this.text = text;
      this.url = url;
      this.rank = rank;
    }
    public String getTitle(){return title;}
    public String getText(){return text;}
    public String getUrl(){return url;}
    public int getRank(){return rank;}
  }
  private final HttpClient http = HttpClient.newBuilder()
    .connectTimeout(Duration.ofSeconds(10))
    .build();
  public List<Passage> search(StringQuery){
    if(query == null || query.isBlank()){
      return new ArrayList<>();
    }
    try{
      String url = "https://en/wikipedia.org/w/api.php?action=query&format=json"
        + "&generator=search&gsrlimit=38&gsrsearch=" + encode(query)
        + "&prop=extracs&exintro=1&explaintext=1&exlimit=max";
      HttpRequest request = HttpRequest.newBuilder(URI.create(url))
        .timeout(Duration.ofSeconds(10))
        .header("User-Agent", "HallucinationDetector/1.0 (project)")
        .GET()
        .build();
      HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
      if(response.statusCode() != 200){
        return new ArrayList<>();
      }
      return parsePassages(response.body());
    }catch(Exception e){
      return new ArrayList<>();  
    }
  }
  static List<Passage> parsePassages(String json){
    List<Passage> list = new ArrayList<>();
    String[] pieces = json.split("\"pageid\":");
    for(int i = 1; i < pieces.length; i++){
      String piece = pieces[i];
      String title = readText(piece, "title");
      String text = readText(piece, "extract");
      int rank = readNumber(piece, "index");
      if(title.isEmpty() || text.isBlank()){
        continue;
      }
      list.add(new Passage(title, text.trim(), makeUrl(title), rank));
    }
    list.sort((a, b) -> Integer.compare(a.getRank(), b.getRank()));
    return list;
  }
