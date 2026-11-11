package com.example.hallucinationdetector.service; 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import org.junit.jupiter.api.Test;
 
class ServiceLogicTests {
    static class MovableClock extends Clock {
        long now = 1_000_000L;
        @Override public ZoneId getZone() { return ZoneId.of("UTC"); }
        @Override public Clock withZone(ZoneId zone) { return this; }
        @Override public Instant instant() { return Instant.ofEpochMilli(now); }
    }
 
    @Test
    void auditLogAppendsLinesAndBlocksForgedLines() throws IOException {
        Path file = Files.createTempDirectory("audit").resolve("audit.log");
        AuditLogger audit = new AuditLogger(file);
        audit.log("first event");
        audit.log("second event\nFAKE LINE injected");
        List<String> lines = Files.readAllLines(file);
        assertEquals(2, lines.size());                       // the injected line break was removed
        assertTrue(lines.get(0).endsWith("first event"));
        assertTrue(lines.get(1).contains("second event FAKE LINE injected"));
    }
 
    @Test
    void loginIsLockedAfterFiveFailuresAndUnlocksLater() {
        MovableClock clock = new MovableClock();
        LoginAttemptService attempts = new LoginAttemptService(clock);
        for (int i = 0; i < LoginAttemptService.MAX_FAILURES - 1; i++) {
            attempts.recordFailure("TestUser");
        }
        assertFalse(attempts.isLocked("testuser"));              
        attempts.recordFailure("TESTUSER");
        assertTrue(attempts.isLocked("testuser"));               
        assertFalse(attempts.isLocked("someone-else"));
 
        clock.now += LoginAttemptService.WINDOW_MILLIS + 1;  
        assertFalse(attempts.isLocked("testuser"));
    }
 
    @Test
    void goodLoginClearsFailures() {
        LoginAttemptService attempts = new LoginAttemptService(new MovableClock());
        for (int i = 0; i < 4; i++) attempts.recordFailure("sam");
        attempts.recordSuccess("sam");
        attempts.recordFailure("sam");
        assertFalse(attempts.isLocked("sam"));
    }
 
    @Test
    void evaluationCountsCaughtMissedAndFalseAlarms() throws IOException {
        EvaluationService service = new EvaluationService(
                new AuditLogger(Files.createTempDirectory("eval").resolve("audit.log")));
        List<String> sources = List.of("Python was first released by Guido van Rossum in 1991.");
        List<String> tests = List.of(
                "supported|Python was first released in 1991.",
                "contradicted|Python was first released in 2005.",
                "unsupported|Rust has a mountain mascot.",          
                "garbage line without a bar",                        
                "wronglabel|Some claim here.");                    
        EvaluationReport report = service.evaluate(tests, sources);
        assertEquals(3, report.getTotalItems());
        assertEquals(2, report.getTruePositives());
        assertEquals(1, report.getTrueNegatives());
        assertEquals(0, report.getFalseNegatives());
        assertEquals(100, report.getRecallPercent());
        assertEquals(100, report.getAccuracyPercent());
    }
    @Test
    void fullEvaluationRunsOnTheRealFiles() throws IOException {
        EvaluationService service = new EvaluationService(
                new AuditLogger(Files.createTempDirectory("eval2").resolve("audit.log")));
        EvaluationReport report = service.runEvaluation();
        assertTrue(report.getTotalItems() >= 10);
        assertTrue(report.getRecallPercent() >= 70);
    }
}
