package com.sarva.core;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class LawAgent implements SarvaAgent {

        private final ChatClient chatClient;

        public LawAgent(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
                this.chatClient = chatClientBuilder
                                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore).searchRequest(org.springframework.ai.vectorstore.SearchRequest.builder()
                                    .topK(2)
                                    .filterExpression("version == 'v2'")
                                    .build()).build())
                                .build();
        }

        @Override
        public String getName() {
                return "LAW";
        }

        @Override
        public String handle(String query, ConversationMemory memory) {
                return chatClient.prompt()
                                .system("You are a Legal Expert Agent. Provide accurate legal information based on the provided context. Format your response using Markdown. Use lists for legal points and bold text for statutes or important terms. If the context does not contain the answer, say you don't know based on available records and advise consulting a lawyer.")
                                .user(query)
                                .call()
                                .content();
        }

        @Override
        public Flux<String> handleStream(String query, ConversationMemory memory) {
                return chatClient.prompt()
                                .system("You are a Legal Expert Agent. Provide accurate legal information based on the provided context. Format your response using Markdown. Use lists for legal points and bold text for statutes or important terms. If the context does not contain the answer, say you don't know based on available records and advise consulting a lawyer.")
                                .user(query)
                                .stream()
                                .content();
        }
}
