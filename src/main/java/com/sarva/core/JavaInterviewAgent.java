package com.sarva.core;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class JavaInterviewAgent implements SarvaAgent {

        private final ChatClient chatClient;

        public JavaInterviewAgent(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
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
                return "JAVA_INTERVIEW";
        }

        @Override
        public String handle(String query, ConversationMemory memory) {
                return chatClient.prompt()
                                .system("You are a Senior Java Developer Interview Agent. Help users prepare for Java interviews by providing common interview questions, coding problems, architecture design discussions, and explanations of core concepts based on the provided context. Format your response using Markdown. Use lists for questions, bold text for emphasis, and code blocks for code snippets. If the context does not contain the answer, say you don't know based on available records.")
                                .user(query)
                                .call()
                                .content();
        }

        @Override
        public Flux<String> handleStream(String query, ConversationMemory memory) {
                return chatClient.prompt()
                                .system("You are a Senior Java Developer Interview Agent. Help users prepare for Java interviews by providing common interview questions, coding problems, architecture design discussions, and explanations of core concepts based on the provided context. Format your response using Markdown. Use lists for questions, bold text for emphasis, and code blocks for code snippets. If the context does not contain the answer, say you don't know based on available records.")
                                .user(query)
                                .stream()
                                .content();
        }
}
