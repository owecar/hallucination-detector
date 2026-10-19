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
    private static final int max_claims = 8;
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
          if(!sent.isEmpty()){
            String reply = llm.askLLM(buildPrompt(claimTexts, sent, evidence));
              if(reply == null || reply.startsWith("Error:")){
                String why = reply == null ? "no reply" : shorten(reply, 160);
                for(int k = 0; k < sent.size(); k++){
                  int i = sent.get(k);
                  out[i] = unchecked(claimTexts.get(i), "Verification step was unavailable (" + why + ").");
                }
              }
              else{
                Map<Integer, Judgement> verdicts = parseJudgements(reply);
                for(int k = 0; k < sent.size(); k++){
                  int i = sent.get(k);
                  Judgement j = verdicts.get(k + 1);
                  out[i] = j == null ? unchecked(claimTexts.get(i), "Judge returned no verdict for claim") : toClaim(claimTexts.get(i), evidence.get(k), j);
                }
              }
          }
          return new ArrayList<>Arrays.asList(out));
    }
    static boolean isCheckable(String text){
      if(text == null) return false;
      String t = text.trim();
      return t.split("\\s+").length >= 5 && !t.endsWith("?") && !Filler.matcher(t).matches();
    }
    static String buildPrompt(List<String> claims, List<Integer> sent, List<List<passage>> evidence){
      StringBuilder sb = new StringBuilder();
      sb.append("You are a strict fact cheker for computer science claims")
      .append("For each numbered claim use ONLY the evidence passages listed for that claim")
      .append("choose a label: SUPPORTED if the evidence states or clearly implies the claim")
      .append("CONTRADICTED if the evidence states something that conflicts with the claim")
      .append("NOT_ENOUGH_INFO if evidence does not address the claim")
      .append("give a confidence rating from 0.0 to 1.0 for your label, and the number of the passage you relied on (0 if none)")
      .append("reply with exactly one line per claim in this format and nothing else")
      .append("claim_number | label | confidence | passage_number | short reason. do not use markdown");
      for(int k = 0; k < sent.size(); k++){
        sb.append(" claim").append(k + 1).append(":")
          .append(oneLine(claims.get(sent.get(k)), 400).replace('|', '/'));
        List<Passage> ps = evience.get(k);
        for(int p = 0; p < ps.size(); p++){
          sb.append(" evience ").append(k+1).append('.').append(p+1)
            .append(" (").append(ps.get(p).title()).append("): ")
            .append(oneLine(ps.get(p).text(), 700));
        }
      }
      return sb.toString();
    }
    static Map<Integer, Judgement> parseJudgements(String reply){
      Map<Integer, Judgement> map = new HashMap<>();
      for(String line : reply.split("\\r?\\n")){
        Matcher m = LINE.matcher(line.trim());
        if(!m.matches()) continue;
        String label = normalizeLabel(m.group(2));
        if(label == null) continue;
        double conf = Double.parseDouble(m.group(3));
        if(conf > 1.0) conf = conf / 100.0;
        conf = Math.max(0.0, Math.min(1.0, conf));
        map.put(Integer.parseInt(m.group(1)),
                new Judgement(label, conf, Integer.parseInt(m.group(4)), m.group(5).trim()));
      }
      return map;
    }

