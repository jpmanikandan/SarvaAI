package com.sarva.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class FinanceAgentTest {

    private FinanceAgent financeAgent;

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    @Mock
    private VectorStore vectorStore;

    @Mock
    private GoldSilverService goldSilverService;

    private ConversationMemory memory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(chatClientBuilder.defaultAdvisors(any(org.springframework.ai.chat.client.advisor.api.Advisor[].class)))
                .thenReturn(chatClientBuilder);
        when(chatClientBuilder.build()).thenReturn(chatClient);
        financeAgent = new FinanceAgent(chatClientBuilder, vectorStore, goldSilverService);
        memory = new ConversationMemory();

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(anyString())).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
    }

    @Test
    void testGoldPriceQuery() {
        String query = "current gold rate in Chennai";
        when(goldSilverService.getCurrentPrices("Chennai")).thenReturn("Gold Price: 6000");

        String response = financeAgent.handle(query, memory);

        assertEquals("Gold Price: 6000", response);
        verify(goldSilverService).getCurrentPrices("Chennai");
        assertEquals("Chennai", memory.getEntity("city"));
        assertEquals("24K", memory.getEntity("metal"));
    }

    @Test
    void testGoldPredictionQuery() {
        String query = "predict gold rate tomorrow in Trichy";
        when(goldSilverService.predictTomorrow("Trichy", "24K")).thenReturn("Predicted: 6100");

        String response = financeAgent.handle(query, memory);

        assertEquals("Predicted: 6100", response);
        verify(goldSilverService).predictTomorrow("Trichy", "24K");
    }

    @Test
    void testGeneralFinanceRagQuery() {
        String query = "Explain mutual funds";
        List<Document> docs = Collections.singletonList(new Document("Mutual funds are investment vehicles..."));

        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(docs);
        when(callResponseSpec.content()).thenReturn("AI Response about Mutual Funds");

        String response = financeAgent.handle(query, memory);

        assertTrue(response.contains("AI Response"));
        verify(vectorStore).similaritySearch(any(SearchRequest.class));
    }
}
