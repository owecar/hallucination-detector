package com.example.hallucinationdetector.factcheck; 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
 
public final class SampleData {
    public static final String SOURCES_FILE = "/sources.txt";
    public static final String TESTDATA_FILE = "/testdata.csv";
    private SampleData() {
    }
    /** trusted sentences the Fact Checker compares claims against. */
    public static List<String> getSources() {
        return readLines(SOURCES_FILE);
    }
    /** reads classpath text file into list. A missing file gives an empty list*/
    public static List<String> readLines(String resourcePath) {
        List<String> lines = new ArrayList<>();
        try (InputStream in = SampleData.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                System.getLogger(SampleData.class.getName())
                        .log(System.Logger.Level.WARNING, "File not found: " + resourcePath);
                return lines;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                    lines.add(trimmed);
                }
            }
        } catch (IOException e) {
            System.getLogger(SampleData.class.getName())
                    .log(System.Logger.Level.WARNING, "Could not read " + resourcePath + ": " + e.getMessage());
        }
        return lines;
    }
}
