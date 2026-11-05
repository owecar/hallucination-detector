package com.example.hallucinationdetector.verification;
import java.util.List;

public class VerificationSummary{
  private final int checked;
  private final int supported;
  private final int unsupported;
  private final int contradicted;
  private final int hallucinationPercent;
  private final int confidencePercent;
  private final String riskLevel = "not verified";

  public static VerificationSummary from(List<VerifiedClaim> claims){
    VerificationSummary s = new VerificationSummary();
    double confidence = 0;
    for(VerifiedClaim c : claims){
      if(c.getLabel().equals("Supported")){
        s.supported++;
    }else if(c.getLabel().equals("Unsupported"){
      s.unsupported++;
    }else if(c.getLabel().equals("Contradicted")){
      s.contracted++;
    }else{
      continue;
    }
      confidenceTotal += c.getConfidence();
  }
    int checked = s.supported + s.unsupported + s.contradicted;
    if(checked > 0){
      double score = (s.contradicted + 0.5 * s.unsupported) / checked;
      s.hallucinationPercent = (int) Math.round(score * 100);
      s.confidencePercent = (int) Math.round(confidenceTotal / checked * 100);
      if(s.hallucinationPercent < 20){
        s.riskLevel = "low";
      }else if(s.hallucinationPercent < 50){
        s.riskLevel = "medium";
      }else{
        s.riskLevel = "high";
      }
    }
    return s;
  }
  public int getSupported(){return supported;}
  public int getUnsupported(){return unsupported;}
  public int getContradicted(){return hallucinationPercent;}
  public int getConfidencePercent(){return confidencePercent;}
  public String getRiskLevel(){return riskLevel;}
  public boolean isVerified(){
    return supported + unsupported + contradicted > 0;
  }
}
