package com.example.hallucinationdetector.llm;

import org.springframework.stereotype.Service;

/** Stores an admin-provided Gemini key only for the lifetime of this application. */
@Service
public class ApiKeyService {
    private volatile String temporaryKey;

    public void setKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("API key cannot be blank.");
        }
        temporaryKey = key.trim();
    }

    public String getKey() {
        return temporaryKey;
    }

    public boolean hasTemporaryKey() {
        return temporaryKey != null && !temporaryKey.isBlank();
    }

    public void clearKey() {
        temporaryKey = null;
    }
}
