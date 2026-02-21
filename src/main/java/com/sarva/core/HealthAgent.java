package com.sarva.core;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class HealthAgent implements SarvaAgent {

        private final ChatClient chatClient;

        public HealthAgent(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
                this.chatClient = chatClientBuilder
                                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore).searchRequest(org.springframework.ai.vectorstore.SearchRequest.builder()
                                    .topK(2)
                                    .filterExpression("version == 'v2'")
                                    .build()).build())
                                .build();
        }

        @Override
        public String getName() {
                return "HEALTH";
        }

        @Override
        public String handle(String query, ConversationMemory memory) {
                return chatClient.prompt()
                                .system("You are a Health Expert Agent. Provide health and wellness information based on the provided context. Format your response using Markdown. Use lists and bold text for important advice. If the context does not contain the answer, say you don't know based on available records and advise consulting a healthcare professional.")
                                .user(query)
                                .call()
                                .content();
        }

        @Override
        public Flux<String> handleStream(String query, ConversationMemory memory) {
                return chatClient.prompt()
                                .system("You are a Health Expert Agent. Provide health and wellness information based on the provided context. Format your response using Markdown. Use lists and bold text for important advice. If the context does not contain the answer, say you don't know based on available records and advise consulting a healthcare professional.")
                                .user(query)
                                .stream()
                                .content();
        }
}
