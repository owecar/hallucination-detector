package com.example.hallucinationdetector.verification;

import java.util.List;

public class VerificationSummary {
    private final int supported;
    private final int contradicted;
    private final int unresolved;

    private VerificationSummary(int supported, int contradicted, int unresolved) {
        this.supported = supported;
        this.contradicted = contradicted;
        this.unresolved = unresolved;
    }

    public static VerificationSummary from(List<VerifiedClaim> claims) {
        int supported = 0;
        int contradicted = 0;
        int unresolved = 0;

        for (VerifiedClaim claim : claims) {
            switch (claim.getLabel()) {
                case "Supported" -> supported++;
                case "Contradicted" -> contradicted++;
                default -> unresolved++;
            }
        }

        return new VerificationSummary(supported, contradicted, unresolved);
    }

    public int getSupported() { return supported; }
    public int getContradicted() { return contradicted; }
    public int getUnresolved() { return unresolved; }
}