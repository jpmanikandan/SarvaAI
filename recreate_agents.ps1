$agents = @{
    "DevAgent"         = "You are a Development Expert Agent. Provide software development insights, coding best practices, and technical solutions based on the provided context. If the context does not contain the answer, say you don't know based on available records."
    "HealthAgent"      = "You are a Health Expert Agent. Provide health and wellness information based on the provided context. If the context does not contain the answer, say you don't know based on available records and advise consulting a healthcare professional."
    "EduAgent"         = "You are an Education Expert Agent. Provide educational guidance, learning strategies, and academic information based on the provided context. If the context does not contain the answer, say you don't know based on available records."
    "CommerceAgent"    = "You are a Commerce Expert Agent. Provide e-commerce insights, business strategies, and market information based on the provided context. If the context does not contain the answer, say you don't know based on available records."
    "IoTAgent"         = "You are an IoT Expert Agent. Provide Internet of Things insights, smart device information, and IoT solutions based on the provided context. If the context does not contain the answer, say you don't know based on available records."
    "LogAnalyzerAgent" = "You are a Log Analyzer Expert Agent. Provide insights from log data, identify patterns, and suggest solutions based on the provided context. If the context does not contain the answer, say you don't know based on available records."
}

foreach ($agent in $agents.Keys) {
    $systemPrompt = $agents[$agent]
    $content = @"
package com.sarva.core;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class $agent implements SarvaAgent {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public $agent(ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
    }

    @Override
    public String getName() {
        return "$($agent.Replace('Agent','').ToUpper())";
    }

    @Override
    public String handle(String query, ConversationMemory memory) {
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(2)
                        .filterExpression("version == 'v2'")
                        .build());

        String context = documents.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n"));

        return chatClient.prompt()
                .system("$systemPrompt")
                .user("Context:\n" + context + "\n\nQuestion: " + query)
                .call()
                .content();
    }

    @Override
    public Flux<String> handleStream(String query, ConversationMemory memory) {
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(2)
                        .filterExpression("version == 'v2'")
                        .build());


        String context = documents.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n"));

        return chatClient.prompt()
                .system("$systemPrompt")
                .user("Context:\n" + context + "\n\nQuestion: " + query)
                .stream()
                .content();
    }
}
"@
    
    Set-Content -Path "src\main\java\com\sarva\core\$agent.java" -Value $content
    Write-Host "Created $agent"
}

Write-Host "All agents recreated successfully"
