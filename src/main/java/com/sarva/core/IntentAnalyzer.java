package com.sarva.core;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class IntentAnalyzer {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public IntentAnalyzer(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public static class IntentResponse {
        private String language;
        private String englishQuery;
        private String category;

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getEnglishQuery() {
            return englishQuery;
        }

        public void setEnglishQuery(String englishQuery) {
            this.englishQuery = englishQuery;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }
    }

    public IntentResponse analyze(String query) {
        String systemPrompt = "Analyze the user query. Output ONLY a valid JSON object with these fields: " +
                "1. 'language': The detected language (e.g., 'Tamil', 'English'). " +
                "2. 'englishQuery': The query translated to English. " +
                "3. 'category': One of: LAW, DEV, FINANCE, HEALTH, EDU, MATRIMONY, IOT, COMMERCE, YOGA, GYM, DIET, JAVA_INTERVIEW, SSLC, TWELFTH, LOG_ANALYZER, GENERAL. "
                +
                "Example output: {\"language\": \"Tamil\", \"englishQuery\": \"What is the gold rate?\", \"category\": \"FINANCE\"}";

        String response = chatClient.prompt()
                .system(systemPrompt)
                .user(query)
                .call()
                .content();

        try {
            // Clean response if LLM adds markdown backticks
            String cleaned = response.replaceAll("```json", "").replaceAll("```", "").trim();
            return objectMapper.readValue(cleaned, IntentResponse.class);
        } catch (Exception e) {
            System.err.println("Failed to parse intent: " + response);
            IntentResponse fallback = new IntentResponse();
            fallback.setLanguage("English");
            fallback.setEnglishQuery(query);
            fallback.setCategory("GENERAL");
            return fallback;
        }
    }
}
