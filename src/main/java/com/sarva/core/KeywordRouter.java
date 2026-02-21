package com.sarva.core;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class KeywordRouter {

    private static final Map<String, String> KEYWORD_MAP = new HashMap<>();

    static {
        // Finance
        KEYWORD_MAP.put("gold", "FINANCE");
        KEYWORD_MAP.put("silver", "FINANCE");
        KEYWORD_MAP.put("price", "FINANCE");
        KEYWORD_MAP.put("rate", "FINANCE");
        KEYWORD_MAP.put("prediction", "FINANCE");
        KEYWORD_MAP.put("market", "FINANCE");
        KEYWORD_MAP.put("stock", "FINANCE");
        KEYWORD_MAP.put("shares", "FINANCE");
        KEYWORD_MAP.put("investment", "FINANCE");
        KEYWORD_MAP.put("bank", "FINANCE");

        // Legal
        KEYWORD_MAP.put("law", "LAW");
        KEYWORD_MAP.put("legal", "LAW");
        KEYWORD_MAP.put("court", "LAW");
        KEYWORD_MAP.put("property", "LAW");
        KEYWORD_MAP.put("case", "LAW");
        KEYWORD_MAP.put("ipc", "LAW");
        KEYWORD_MAP.put("section", "LAW");
        KEYWORD_MAP.put("police", "LAW");
        KEYWORD_MAP.put("advocate", "LAW");
        KEYWORD_MAP.put("crime", "LAW");

        // Dev
        KEYWORD_MAP.put("code", "DEV");
        KEYWORD_MAP.put("java", "DEV");
        KEYWORD_MAP.put("python", "DEV");
        KEYWORD_MAP.put("debug", "DEV");
        KEYWORD_MAP.put("error", "DEV");
        KEYWORD_MAP.put("spring", "DEV");

        // Health
        KEYWORD_MAP.put("health", "HEALTH");
        KEYWORD_MAP.put("doctor", "HEALTH");
        KEYWORD_MAP.put("symptom", "HEALTH");
        KEYWORD_MAP.put("medicine", "HEALTH");
        KEYWORD_MAP.put("disease", "HEALTH");

        // Education
        KEYWORD_MAP.put("learn", "EDU");
        KEYWORD_MAP.put("education", "EDU");
        KEYWORD_MAP.put("study", "EDU");
        KEYWORD_MAP.put("physics", "EDU");
        KEYWORD_MAP.put("math", "EDU");
        KEYWORD_MAP.put("school", "EDU");

        // SSLC / 12th
        KEYWORD_MAP.put("sslc", "SSLC");
        KEYWORD_MAP.put("10th", "SSLC");
        KEYWORD_MAP.put("twelfth", "TWELFTH");
        KEYWORD_MAP.put("12th", "TWELFTH");
        KEYWORD_MAP.put("hsc", "TWELFTH");

        // Matrimony
        KEYWORD_MAP.put("marriage", "MATRIMONY");
        KEYWORD_MAP.put("partner", "MATRIMONY");
        KEYWORD_MAP.put("match", "MATRIMONY");
        KEYWORD_MAP.put("bride", "MATRIMONY");
        KEYWORD_MAP.put("groom", "MATRIMONY");

        // IoT
        KEYWORD_MAP.put("iot", "IOT");
        KEYWORD_MAP.put("automation", "IOT");
        KEYWORD_MAP.put("sensor", "IOT");
        KEYWORD_MAP.put("device", "IOT");
        KEYWORD_MAP.put("smart", "IOT");

        // Fitness
        KEYWORD_MAP.put("yoga", "YOGA");
        KEYWORD_MAP.put("gym", "GYM");
        KEYWORD_MAP.put("protein", "DIET");
        KEYWORD_MAP.put("diet", "DIET");
        KEYWORD_MAP.put("workout", "GYM");
        KEYWORD_MAP.put("exercise", "GYM");
        KEYWORD_MAP.put("calories", "DIET");
    }

    public Optional<String> fastClassify(String query) {
        if (query == null || query.isBlank())
            return Optional.empty();

        String lowerQuery = query.toLowerCase();

        // Check for direct matches
        for (Map.Entry<String, String> entry : KEYWORD_MAP.entrySet()) {
            if (lowerQuery.contains(entry.getKey())) {
                return Optional.of(entry.getValue());
            }
        }

        return Optional.empty();
    }
}
