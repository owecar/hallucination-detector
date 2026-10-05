package com.example.hallucinationdetector.detector;

import com.example.hallucinationdetector.factcheck.VerificationResult;

public class HallucinationDetector {
    public boolean isHallucination(VerificationResult result) {
        if ("contradicted".equalsIgnoreCase(result.label())) return true;
        if ("unsupported".equalsIgnoreCase(result.label())) return true;
        return result.score < 0.5;
    }
}
