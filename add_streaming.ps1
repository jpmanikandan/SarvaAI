$agents = @(
    "HealthAgent",
    "LawAgent",
    "DevAgent",
    "EduAgent",
    "CommerceAgent",
    "IoTAgent",
    "MatrimonyAgent"
)

$streamMethod = @'

    @Override
    public Flux<String> handleStream(String query, ConversationMemory memory) {
        // Use streaming for RAG retrieval
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .topK(3)
                        .query(query)
                        .build());

        String context = documents.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n"));

        return chatClient.prompt()
                .system(systemPrompt())
                .user("Context:\n" + context + "\n\nQuestion: " + query)
                .stream()
                .content();
    }

    private String systemPrompt() {
        return chatClient.prompt()
                .system("")
                .call()
                .content();
    }
'@

foreach ($agent in $agents) {
    $filePath = "src\main\java\com\sarva\agent\$agent.java"
    if (Test-Path $filePath) {
        $content = Get-Content $filePath -Raw
        
        # Add Flux import if not present
        if ($content -notmatch "import reactor.core.publisher.Flux") {
            $content = $content -replace "(import org.springframework.stereotype.Service;)", "`$1`r`nimport reactor.core.publisher.Flux;"
        }
        
        # Find the last closing brace and add the method before it
        $lastBrace = $content.LastIndexOf("}")
        $beforeBrace = $content.Substring(0, $lastBrace)
        $afterBrace = $content.Substring($lastBrace)
        
        $newContent = $beforeBrace + $streamMethod + "`r`n" + $afterBrace
        
        Set-Content -Path $filePath -Value $newContent
        Write-Host "Updated $agent"
    }
}

Write-Host "All agents updated with streaming support"
