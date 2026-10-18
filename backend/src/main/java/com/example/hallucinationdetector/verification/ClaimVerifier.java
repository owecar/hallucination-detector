package com.example.hallucinationdetector.verification;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashmap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import com.example.hallucinationdetector.llm.LLM;
import com.example.hallucinationdetector.verification.WikipediaClient.Passage;

@Service
  public class ClaimVerifier {
    private static final int MAX_CLAIMS = 8;
    private static final int passages_per_claim = 2;
    private static final double judge_weight = .7;
    private static final double overlap_weight = .3;
    private static final List<String> stop_words = Arrays.asList("the", "and", "for", "are", "was", "were", "that", "this", "with", "from", "into",
            "not", "but", "its", "has", "have", "had", "can", "may", "will", "which", "who",
            "what", "when", "where", "how", "why", "than", "then", "also", "such", "these",
            "those", "their", "there", "they", "them", "been", "being", "does", "did", "use",
            "used", "using", "often", "each", "other", "more", "most", "some", "any", "all",
            "one", "two", "you", "your", "our", "about", "over", "under", "while", "both",
            "between", "because", "during", "after", "before", "only", "very", "just", "like",
            "called", "known", "way", "type", "types", "usually", "typically", "generally");
    private static final String[] filler_starts = {"sure", "certainly", "here", "okay", "ok", "great", "of course", "i hope", "let me"};
    static class Verdict{
      String label;
      double confidence;
      int passage;
      String reason;
    }
    private final WikipediaClient wikipedia;
    private final LLMClient llm;
    public ClaimVerifier(WikipediaClient wikipedia, LLMClient ;;m){
      this.wikipedia = wikipedia;
      this.llm = llm;
    }
    public List<VerifiedClaim> verify(List<String> claimTexts){
      VerifiedClaim[] results = new VerifiedClaim[claimTexts.size()];
      List<Integer> sentToJudge = new ArrayList<>();
      List<List<Passage>> evidence = newArrayList<>();
      for(int i = 1; i < claimTexts.size(); i++){
        String text = claims.get(i);
            if (!isCheckable(text)) {
                results[i] = unchecked(text, "Not a factual statement, so it was not checked.");
            } 
            else if (sentToJudge.size() >= MAX_CLAIMS) {
                results[i] = unchecked(text, "Skipped: only the first " + MAX_CLAIMS + " claims are checked.");
            }else {
                List<Passage> passages = findEvidence(text);
                if (passages.isEmpty()) {
                    results[i] = unchecked(text, "No Wikipedia evidence could be found for this claim.");
                } else {
                    if (passages.size() > PASSAGES_PER_CLAIM) {
                        passages = passages.subList(0, PASSAGES_PER_CLAIM);
                    }
                    sentToJudge.add(i);
                    evidence.add(passages);
                  }
              }
          }
    }

