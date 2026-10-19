package com.example.hallucinationdetector.verification;
import org.springframework.stereotype.Service;
import com.example.hallucinationdetector.llm.LLMClient;

@Service
  public class TopicValidator{
    private static final String reject_message = "Ask a computer science question (ex: programming, algorithms, networks, databases, etc)";
    private final LLMClient llm;
    public TopicValidator(LLMClient llm){
      this.llm = llm;
    }
    public String check(String question){
      String prompt = "Is the question about computer science or programming? Reply only with Yes or No. Question: " + question.replace('\n', ' ');
      String reply = llm.askLLM(prompt);
      if(reply == null || reply.startsWith("Error:")){
        return null;
      }
      if(reply.trim().toUpperCase().startsWith("No")){
        return reject_message;
      }
      return null;
    }
  }
  
