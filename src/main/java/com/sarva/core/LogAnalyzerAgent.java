package com.sarva.core;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class LogAnalyzerAgent implements SarvaAgent {

    private final ChatClient chatClient;

    public LogAnalyzerAgent(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
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
        return "LOG_ANALYZER";
    }

    @Override
    public String handle(String query, ConversationMemory memory) {
        return chatClient.prompt()
                .system("You are a Java Logs Analysis Expert. Your task is to analyze Java application logs, identify exceptions, find the root cause, and suggest fixes or optimizations. "
                        +
                        "Base your analysis on the provided context. If the context does not contain enough information to provide a specific solution, offer general best practices for the identified issue. "
                        +
                        "Format your response using Markdown, using bold text for exceptions and code blocks for suggested code fixes.")
                .user(query)
                .call()
                .content();
    }

    @Override
    public Flux<String> handleStream(String query, ConversationMemory memory) {
        return chatClient.prompt()
                .system("You are a Java Logs Analysis Expert. Your task is to analyze Java application logs, identify exceptions, find the root cause, and suggest fixes or optimizations. "
                        +
                        "Base your analysis on the provided context. If the context does not contain enough information to provide a specific solution, offer general best practices for the identified issue. "
                        +
                        "Format your response using Markdown, using bold text for exceptions and code blocks for suggested code fixes.")
                .user(query)
                .stream()
                .content();
    }
}
