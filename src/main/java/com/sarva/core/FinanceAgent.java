package com.sarva.core;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class FinanceAgent implements SarvaAgent {

    private final ChatClient chatClient;
    private final GoldSilverService goldSilverService;

    public FinanceAgent(ChatClient.Builder chatClientBuilder, VectorStore vectorStore,
            GoldSilverService goldSilverService) {
        this.chatClient = chatClientBuilder
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore)
                        .searchRequest(org.springframework.ai.vectorstore.SearchRequest.builder()
                            .topK(2) // Safe to increase back to 2 with small chunks, but sticking to 1 or 2 is fine. I'll use 2.
                            .filterExpression("version == 'v2'")
                            .build())
                        .build())
                .build();
        this.goldSilverService = goldSilverService;
    }

    @Override
    public String getName() {
        return "FINANCE";
    }

    @Override
    public String handle(String query, ConversationMemory memory) {
        String q = query.toLowerCase();

        // Tool Selection
        if (q.contains("gold") || q.contains("silver") || q.contains("rate") || q.contains("price")) {
            String city = extractCity(q, memory);
            String info;
            if (q.contains("predict") || q.contains("tomorrow") || q.contains("next")) {
                String metal = extractMetal(q, memory);
                info = goldSilverService.predictTomorrow(city, metal);
            } else {
                info = goldSilverService.getCurrentPrices(city);
            }

            return chatClient.prompt()
                    .system("You are a Finance Expert Agent. Provide the live price/prediction information below to the user with an expert analysis. Always include a disclaimer. Respond in Markdown.")
                    .user("Information: " + info + "\n\nUser Question: " + query)
                    .call()
                    .content();
        }

        // RAG path (handled by QuestionAnswerAdvisor + ChatHistoryAdvisor)
        return chatClient.prompt()
                .system("You are a Finance Expert Agent. Use the provided context to answer questions about finance, markets, and investment. Always include a disclaimer.")
                .user(query)
                .call()
                .content();
    }

    @Override
    public Flux<String> handleStream(String query, ConversationMemory memory) {
        String q = query.toLowerCase();

        if (q.contains("gold") || q.contains("silver") || q.contains("rate") || q.contains("price")) {
            return Flux.defer(() -> Flux.just(handle(query, memory)));
        }

        return chatClient.prompt()
                .system("You are a Finance Expert Agent. Use the provided context to answer questions about finance. Always include a disclaimer.")
                .user(query)
                .stream()
                .content();
    }

    private String extractCity(String q, ConversationMemory memory) {
        if (q.contains("chennai"))
            return "Chennai";
        if (q.contains("madurai"))
            return "Madurai";
        if (q.contains("trichy"))
            return "Trichy";
        if (q.contains("salem"))
            return "Salem";
        if (q.contains("coimbatore"))
            return "Coimbatore";
        String stored = memory.getEntity("city");
        return stored != null ? stored : "Trichy";
    }

    private String extractMetal(String q, ConversationMemory memory) {
        if (q.contains("silver"))
            return "SILVER";
        if (q.contains("18"))
            return "18K";
        if (q.contains("22"))
            return "22K";
        String stored = memory.getEntity("metal");
        return stored != null ? stored : "24K";
    }
}
