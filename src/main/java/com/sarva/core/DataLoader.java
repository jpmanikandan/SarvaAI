package com.sarva.core;

import java.util.List;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import jakarta.annotation.PostConstruct;

@Configuration
public class DataLoader {

    @Autowired
    private VectorStore vectorStore;

    @PostConstruct
    public void init() {
        // Strict splitting: 300 tokens max to avoid context overflow
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter(300, 300, 5, 10000, true);

        String[] files = { "law.txt", "finance.txt", "dev.txt", "health.txt", "edu.txt", "matrimony.txt", "iot.txt",
                "commerce.txt", "yoga.txt", "gym.txt", "diet.txt", "java_interview.txt", "sslc.txt", "twelfth.txt",
                "log_analyzer.txt" };

        System.out.println("Starting parallel RAG verification...");
        long start = System.currentTimeMillis();

        java.util.Arrays.stream(files).parallel().forEach(fileName -> {
            try {
                // Check if file is already ingested with new version 'v2'
                List<Document> existing = vectorStore.similaritySearch(
                        SearchRequest.builder()
                                .query(fileName)
                                .topK(1)
                                .filterExpression("source == '" + fileName + "' && version == 'v2'")
                                .build());

                if (!existing.isEmpty()) {
                    System.out.println("[SKIP] Already indexed (v2): " + fileName);
                    return;
                }

                System.out.println("[INGEST] Processing (v2): " + fileName);
                ClassPathResource resource = new ClassPathResource(fileName);
                if (resource.exists()) {
                    TextReader reader = new TextReader(resource);
                    List<Document> documents = tokenTextSplitter.split(reader.get());

                    // Add metadata
                    for (Document doc : documents) {
                        doc.getMetadata().put("source", fileName);
                        doc.getMetadata().put("version", "v2"); // New version tag
                    }

                    vectorStore.add(documents);
                    System.out.println("[SUCCESS] Ingested " + documents.size() + " chunks (v2) from " + fileName);
                } else {
                    System.err.println("[ERROR] File not found: " + fileName);
                }
            } catch (Exception e) {
                System.err.println("[ERROR] Failed to ingest " + fileName + ": " + e.getMessage());
            }
        });

        long end = System.currentTimeMillis();
        System.out.println("RAG verification/ingestion completed in " + (end - start) + "ms.");
    }
}
