package com.example.hallucinationdetector.factcheck;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
/**
 * compares one claim against a list of trusted source sentences
 * and returns a label: "supported", "unsupported" or "contradicted".
 * How it works:
 *   1. Break the claim into its important words (stop words such as "the" and "was" are dropped).
 *   2. For every source sentence, score = share of the claim's important words found in it.
 *   3. The source with the highest score is the best source
 *   4. If the best source has too little overlap: unsupported (no evidence)
 *      If the claim and the best source have different numbers: contradicted
 *      If the overlap is high enough: supported
 *      Otherwise: unsupported
 */
public class FactChecker {

    public static final String SUPPORTED = "supported";
    public static final String UNSUPPORTED = "unsupported";
    public static final String CONTRADICTED = "contradicted";
    public static final String NO_SOURCE = "No source available";
    public static final double SUPPORT_THRESHOLD = 0.6;
    public static final double RELEVANCE_THRESHOLD = 0.3;

    private static final Set<String> STOP_WORDS = Set.of(
            "a", "an", "the", "is", "are", "was", "were", "be", "been", "of", "in", "on", "at", "by",
            "for", "to", "and", "or", "it", "its", "that", "this", "as", "with", "from", "has", "have",
            "had", "which", "who", "than", "then", "so", "not", "but", "into", "also");

    public VerificationResult verifyClaim(String claim, List<String> sources) {
        Set<String> claimWords = tokenize(claim);
        String bestSource = NO_SOURCE;
        double bestScore = 0.0;

        if (sources != null && !claimWords.isEmpty()) {
            for (String source : sources) {
                double score = coverage(claimWords, tokenize(source));
                if (score > bestScore) {
                    bestScore = score;
                    bestSource = source;
                }
            }
        }

        if (bestScore < RELEVANCE_THRESHOLD) {
            return new VerificationResult(NO_SOURCE, bestScore, UNSUPPORTED);
        }
        if (numbersConflict(claimWords, tokenize(bestSource))) {
            return new VerificationResult(bestSource, bestScore, CONTRADICTED);
        }
        String label = bestScore >= SUPPORT_THRESHOLD ? SUPPORTED : UNSUPPORTED;
        return new VerificationResult(bestSource, bestScore, label);
    }

    /** Share (0.0 to 1.0) of the claim's words that also appear in the source. */
    public double coverage(Set<String> claimWords, Set<String> sourceWords) {
        if (claimWords.isEmpty()) {
            return 0.0;
        }
        int found = 0;
        for (String word : claimWords) {
            if (sourceWords.contains(word)) {
                found++;
            }
        }
        return (double) found / claimWords.size();
    }

    /** true when both texts contain numbers (years, sizes, ports) but share none of them. */
    public boolean numbersConflict(Set<String> claimWords, Set<String> sourceWords) {
        List<String> claimNumbers = numbersIn(claimWords);
        List<String> sourceNumbers = numbersIn(sourceWords);
        if (claimNumbers.isEmpty() || sourceNumbers.isEmpty()) {
            return false;
        }
        for (String number : claimNumbers) {
            if (sourceNumbers.contains(number)) {
                return false;
            }
        }
        return true;
    }

    /** lowercase the text and keeps only the important words. */
    public Set<String> tokenize(String text) {
        Set<String> words = new HashSet<>();
        if (text == null) {
            return words;
        }
        for (String word : text.toLowerCase(Locale.ROOT).split("[^a-z0-9]+")) {
            boolean isNumber = word.matches("\\d+");
            if ((word.length() > 1 || isNumber) && !STOP_WORDS.contains(word)) {
                words.add(word);
            }
        }
        return words;
    }
    private List<String> numbersIn(Set<String> words) {
        List<String> numbers = new ArrayList<>();
        for (String word : words) {
            if (word.matches("\\d+")) {
                numbers.add(word);
            }
        }
        return numbers;
    }
}
