package com.sarva.core;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class YogaAgent implements SarvaAgent {

        private final ChatClient chatClient;

        public YogaAgent(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
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
                return "YOGA";
        }

        @Override
        public String handle(String query, ConversationMemory memory) {
                return chatClient.prompt()
                                .system("You are a Yoga Expert Agent. Provide detailed guidance on yoga poses (asanas), breathing techniques (pranayama), meditation, and flexibility exercises based on the provided context. Format your response using Markdown. Use lists for steps and bold text for pose names. If the context does not contain the answer, say you don't know based on available records.")
                                .user(query)
                                .call()
                                .content();
        }

        @Override
        public Flux<String> handleStream(String query, ConversationMemory memory) {
                return chatClient.prompt()
                                .system("You are a Yoga Expert Agent. Provide detailed guidance on yoga poses (asanas), breathing techniques (pranayama), meditation, and flexibility exercises based on the provided context. Format your response using Markdown. Use lists for steps and bold text for pose names. If the context does not contain the answer, say you don't know based on available records.")
                                .user(query)
                                .stream()
                                .content();
        }
}
