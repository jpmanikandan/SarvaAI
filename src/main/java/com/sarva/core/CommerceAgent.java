package com.sarva.core;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class CommerceAgent implements SarvaAgent {

        private final ChatClient chatClient;

        public CommerceAgent(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
                this.chatClient = chatClientBuilder
                                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore)
                                                .searchRequest(org.springframework.ai.vectorstore.SearchRequest
                                                                .builder()
                                                                .topK(2)
                                                                .filterExpression("version == 'v2'")
                                                                .build())
                                                .build())
                                .build();
        }

        @Override
        public String getName() {
                return "COMMERCE";
        }

        @Override
        public String handle(String query, ConversationMemory memory) {
                return chatClient.prompt()
                                .system("You are a Commerce Expert Agent. Provide e-commerce insights, business strategies, and market information based on the provided context. Format your response using Markdown. Use lists for strategies and bold text for key terms. If the context does not contain the answer, say you don't know based on available records.")
                                .user(query)
                                .call()
                                .content();
        }

        @Override
        public Flux<String> handleStream(String query, ConversationMemory memory) {
                return chatClient.prompt()
                                .system("You are a Commerce Expert Agent. Provide e-commerce insights, business strategies, and market information based on the provided context. Format your response using Markdown. Use lists for strategies and bold text for key terms. If the context does not contain the answer, say you don't know based on available records.")
                                .user(query)
                                .stream()
                                .content();
        }
}
