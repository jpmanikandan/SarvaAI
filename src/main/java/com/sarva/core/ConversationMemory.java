package com.sarva.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores conversation history and context for a user session.
 * Enables context-aware responses and follow-up question handling.
 */
public class ConversationMemory implements Serializable {

    private static final int MAX_HISTORY_SIZE = 3;

    private final List<ConversationTurn> history = new ArrayList<>();
    private final Map<String, String> entities = new HashMap<>();
    private String lastCategory;

    public static class ConversationTurn implements Serializable {
        private final String query;
        private final String response;
        private final String category;
        private final long timestamp;

        public ConversationTurn(String query, String response, String category) {
            this.query = query;
            this.response = response;
            this.category = category;
            this.timestamp = System.currentTimeMillis();
        }

        public String getQuery() {
            return query;
        }

        public String getResponse() {
            return response;
        }

        public String getCategory() {
            return category;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    /**
     * Add a conversation turn to history
     */
    public void addTurn(String query, String response, String category) {
        // Truncate response to save tokens (keep first 1000 chars)
        String storedResponse = response.length() > 1000 
            ? response.substring(0, 1000) + "... [truncated]" 
            : response;
            
        history.add(new ConversationTurn(query, storedResponse, category));
        if (history.size() > MAX_HISTORY_SIZE) {
            history.remove(0); // Remove oldest
        }
        lastCategory = category;
    }

    /**
     * Store an entity (e.g., city, metal type)
     */
    public void setEntity(String key, String value) {
        entities.put(key, value);
    }

    /**
     * Get an entity value
     */
    public String getEntity(String key) {
        return entities.get(key);
    }

    /**
     * Get the last category discussed
     */
    public String getLastCategory() {
        return lastCategory;
    }

    /**
     * Get conversation history
     */
    public List<ConversationTurn> getHistory() {
        return new ArrayList<>(history);
    }

    /**
     * Get a context summary for enriching queries
     */
    public String getContextSummary() {
        StringBuilder context = new StringBuilder();

        if (lastCategory != null) {
            context.append("Last topic: ").append(lastCategory).append(". ");
        }

        if (!entities.isEmpty()) {
            context.append("Mentioned: ");
            entities.forEach((key, value) -> context.append(key).append("=").append(value).append(", "));
            context.setLength(context.length() - 2); // Remove trailing ", "
            context.append(". ");
        }

        if (!history.isEmpty()) {
            ConversationTurn last = history.get(history.size() - 1);
            context.append("Last query: \"").append(last.getQuery()).append("\"");
        }

        return context.toString();
    }

    /**
     * Check if this is likely a follow-up question
     */
    public boolean isFollowUpQuestion(String query) {
        String q = query.toLowerCase().trim();

        // Short queries are likely follow-ups
        if (q.split("\\s+").length <= 3) {
            return !history.isEmpty();
        }

        // Questions starting with "what about", "how about", etc.
        return q.matches("^(what about|how about|and|also|what|how|when|where).*")
                && !history.isEmpty();
    }

    /**
     * Enrich a vague query with context
     */
    public String enrichQuery(String query) {
        if (!isFollowUpQuestion(query)) {
            return query;
        }

        StringBuilder enriched = new StringBuilder(query);

        // Add context from last category
        if (lastCategory != null && !query.toLowerCase().contains(lastCategory.toLowerCase())) {
            enriched.append(" (context: ").append(lastCategory);

            // Add relevant entities
            if (lastCategory.equalsIgnoreCase("FINANCE")) {
                String city = entities.get("city");
                String metal = entities.get("metal");
                if (city != null)
                    enriched.append(", city: ").append(city);
                if (metal != null)
                    enriched.append(", metal: ").append(metal);
            }

            enriched.append(")");
        }

        return enriched.toString();
    }
}

