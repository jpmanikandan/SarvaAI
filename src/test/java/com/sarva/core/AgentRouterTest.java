package com.sarva.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class AgentRouterTest {

    @Mock
    private SarvaAgent financeAgent;
    @Mock
    private SarvaAgent lawAgent;
    @Mock
    private IntentAnalyzer intentAnalyzer;
    @Mock
    private TranslationService translationService;
    @Mock
    private KeywordRouter keywordRouter;

    private AgentRouter agentRouter;
    private ConversationMemory memory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(financeAgent.getName()).thenReturn("FINANCE");
        when(lawAgent.getName()).thenReturn("LAW");

        agentRouter = new AgentRouter(
                Arrays.asList(financeAgent, lawAgent),
                intentAnalyzer,
                translationService,
                keywordRouter);
        memory = new ConversationMemory();
    }

    @Test
    void testRoutingToFinanceAgent() {
        String query = "What is the gold rate?";
        when(keywordRouter.fastClassify(anyString())).thenReturn(Optional.of("FINANCE"));
        when(financeAgent.handle(anyString(), any())).thenReturn("Gold rate is ₹6000");

        String response = agentRouter.route(query, "English", "English", memory);

        assertTrue(response.contains("FINANCE"));
        assertTrue(response.contains("Gold rate is ₹6000"));
    }

    @Test
    void testRoutingToLawAgentViaIntentAnalyzer() {
        String query = "What is IPC 302?";
        when(keywordRouter.fastClassify(anyString())).thenReturn(Optional.empty());

        IntentAnalyzer.IntentResponse intent = new IntentAnalyzer.IntentResponse();
        intent.setCategory("LAW");
        intent.setEnglishQuery(query);
        intent.setLanguage("English");

        when(intentAnalyzer.analyze(anyString())).thenReturn(intent);
        when(lawAgent.handle(anyString(), any())).thenReturn("IPC 302 is for murder.");

        String response = agentRouter.route(query, "English", "English", memory);

        assertTrue(response.contains("LAW"));
        assertTrue(response.contains("IPC 302 is for murder."));
    }
}
