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

}
