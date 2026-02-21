package com.sarva.core;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class SslcAgent implements SarvaAgent {

        private final ChatClient chatClient;

        public SslcAgent(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
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
                return "SSLC";
        }

        @Override
        public String handle(String query, ConversationMemory memory) {
                return chatClient.prompt()
                                .system("You are an expert tutor for SSLC (10th Standard) students. Simplify concepts, explain formulas step-by-step, and provide examples specifically from the 10th-grade curriculum (Maths, Science) based on the provided context. Format your response using Markdown. Use step-by-step lists for clarity, bold text for key terms, and code blocks for formulas where appropriate. If the context does not contain the answer, say you don't know based on available records.")
                                .user(query)
                                .call()
                                .content();
        }

        @Override
        public Flux<String> handleStream(String query, ConversationMemory memory) {
                return chatClient.prompt()
                                .system("You are an expert tutor for SSLC (10th Standard) students. Simplify concepts, explain formulas step-by-step, and provide examples specifically from the 10th-grade curriculum (Maths, Science) based on the provided context. Format your response using Markdown. Use step-by-step lists for clarity, bold text for key terms, and code blocks for formulas where appropriate. If the context does not contain the answer, say you don't know based on available records.")
                                .user(query)
                                .stream()
                                .content();
        }
}
