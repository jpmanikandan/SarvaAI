package com.sarva.core;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class DietAgent implements SarvaAgent {

        private final ChatClient chatClient;

        public DietAgent(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
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
                return "DIET";
        }

        @Override
        public String handle(String query, ConversationMemory memory) {
                return chatClient.prompt()
                                .system("You are a Nutritionist and Diet Agent. Provide advice on meal planning, macro/micro-nutrients, weight loss or gain strategies, and healthy eating habits based on the provided context. Format your response using Markdown. Use lists for meal plans, bold text for nutritional facts, and clear headings. If the context does not contain the answer, say you don't know based on available records.")
                                .user(query)
                                .call()
                                .content();
        }

        @Override
        public Flux<String> handleStream(String query, ConversationMemory memory) {
                return chatClient.prompt()
                                .system("You are a Nutritionist and Diet Agent. Provide advice on meal planning, macro/micro-nutrients, weight loss or gain strategies, and healthy eating habits based on the provided context. Format your response using Markdown. Use lists for meal plans, bold text for nutritional facts, and clear headings. If the context does not contain the answer, say you don't know based on available records.")
                                .user(query)
                                .stream()
                                .content();
        }
}
