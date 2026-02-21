
package com.sarva.core;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import java.util.List;

@Service
public class AgentRouter {

    private final List<SarvaAgent> agents;
    private final IntentAnalyzer intentAnalyzer;
    private final TranslationService translationService;
    private final KeywordRouter keywordRouter;

    public AgentRouter(List<SarvaAgent> agents, IntentAnalyzer intentAnalyzer, TranslationService translationService,
            KeywordRouter keywordRouter) {
        this.agents = agents;
        this.intentAnalyzer = intentAnalyzer;
        this.translationService = translationService;
        this.keywordRouter = keywordRouter;
    }

    @Cacheable(value = "agentResponses", key = "{#query, #inputLanguage, #outputLanguage}")
    public String route(String query, String inputLanguage, String outputLanguage, ConversationMemory memory) {
        System.out.println("Processing Query: " + query);

        // --- Fast Path Optimization ---
        String category = keywordRouter.fastClassify(query).orElse(null);
        String processingQuery = query;
        String sourceLang = inputLanguage;

        if (category == null) {
            // Consolidated Intent Analysis
            IntentAnalyzer.IntentResponse intent = intentAnalyzer.analyze(query);
            sourceLang = (inputLanguage != null && !inputLanguage.isBlank()) ? inputLanguage : intent.getLanguage();
            processingQuery = intent.getEnglishQuery();
            category = intent.getCategory();
        } else {
            System.out.println("Fast-path match found for category: " + category);
            if (sourceLang == null || sourceLang.isBlank()) {
                sourceLang = translationService.detectLanguage(query);
            }
            if (!sourceLang.equalsIgnoreCase("English")) {
                processingQuery = translationService.translate(query, sourceLang, "English");
            }
        }

        if (category.equals("GENERAL") && memory.getLastCategory() != null) {
            category = memory.getLastCategory();
        }

        String targetLang = (outputLanguage != null && !outputLanguage.isBlank()) ? outputLanguage : sourceLang;
        System.out.println("Intent: Lang=" + sourceLang + ", Category=" + category);

        final String finalQuery = processingQuery;
        final String finalCategory = category;

        String result = agents.stream()
                .filter(agent -> agent.getName().equalsIgnoreCase(finalCategory))
                .findFirst()
                .map(agent -> agent.handle(finalQuery, memory))
                .orElseGet(() -> "No specific agent found for " + finalCategory + ". Processing as general query.");

        memory.addTurn(query, result, finalCategory);

        if (!targetLang.equalsIgnoreCase("English") && !targetLang.equalsIgnoreCase(sourceLang)) {
            return "[AGENT:" + finalCategory + "] " + translationService.translate(result, "English", targetLang);
        } else if (!sourceLang.equalsIgnoreCase("English") && targetLang.equalsIgnoreCase(sourceLang)) {
            return "[AGENT:" + finalCategory + "] " + translationService.translate(result, "English", sourceLang);
        }

        return "[AGENT:" + finalCategory + "] " + result;
    }

    public Flux<String> routeStream(String query, String inputLanguage, String outputLanguage,
            ConversationMemory memory) {
        System.out.println("Processing Streaming Query: " + query);

        // --- Fast Path Optimization ---
        String category = keywordRouter.fastClassify(query).orElse(null);
        String processingQuery = query;
        String sourceLang = inputLanguage;

        if (category == null) {
            // Consolidated Intent Analysis
            IntentAnalyzer.IntentResponse intent = intentAnalyzer.analyze(query);
            sourceLang = (inputLanguage != null && !inputLanguage.isBlank()) ? inputLanguage : intent.getLanguage();
            processingQuery = intent.getEnglishQuery();
            category = intent.getCategory();
        } else {
            System.out.println("Streaming Fast-path match found for category: " + category);
            if (sourceLang == null || sourceLang.isBlank()) {
                sourceLang = translationService.detectLanguage(query);
            }
            if (!sourceLang.equalsIgnoreCase("English")) {
                processingQuery = translationService.translate(query, sourceLang, "English");
            }
        }

        if (category.equals("GENERAL") && memory.getLastCategory() != null) {
            category = memory.getLastCategory();
        }

        String targetLang = (outputLanguage != null && !outputLanguage.isBlank()) ? outputLanguage : sourceLang;
        System.out.println("Streaming Intent: Lang=" + sourceLang + ", Category=" + category);

        final String finalQuery = processingQuery;
        final String finalCategory = category;
        final String finalTargetLang = targetLang;

        // Routing to specific Agent
        Flux<String> resultStream = agents.stream()
                .filter(agent -> agent.getName().equalsIgnoreCase(finalCategory))
                .findFirst()
                .map(agent -> agent.handleStream(finalQuery, memory))
                .orElseGet(() -> Flux
                        .just("No specific agent found for " + finalCategory + ". Processing as general query."));

        StringBuffer responseBuffer = new StringBuffer();
        String agentPrefix = "[AGENT:" + finalCategory + "] ";

        if (!finalTargetLang.equalsIgnoreCase("English")) {
            return resultStream
                    .collectList()
                    .flatMapMany(chunks -> {
                        String fullResponse = String.join("", chunks);
                        memory.addTurn(query, fullResponse, finalCategory);
                        String translated = translationService.translate(fullResponse, "English", finalTargetLang);
                        return Flux.just(agentPrefix + translated);
                    });
        } else {
            return Flux.concat(
                    Flux.just(agentPrefix),
                    resultStream
                            .doOnNext(chunk -> responseBuffer.append(chunk))
                            .doOnComplete(() -> {
                                memory.addTurn(query, responseBuffer.toString(), finalCategory);
                                System.out.println("Stream completed. Memory updated.");
                            }));
        }
    }
}
