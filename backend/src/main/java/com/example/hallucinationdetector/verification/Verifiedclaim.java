package com.example.hallucinationdetector.verification;
public class VerifiedClaim{
  private final String text, label, source, explanation;
  private final double confidence;

  public VerifiedClaim(String text, String label, String source, String explanation, double confidence){
    this.text = text;
    this.label = label;
    this.source = source;
    this.explanation = explanation;
    this.confidence = confidence;
  }
  public String getText(){return text;}
  public String getLabel(){return label;}
  public String getSource(){return source;}
  public String getExplanation(){return explanation;}
  public double getConfidence(){return confidence;}

  public int getConfidencePercent(){
    return (int) Math.round(confidence * 100);
  }
  public boolean isHallucination(){
    return label.equals("Unsupported") || label.equals("Contradicted");
  }
}
