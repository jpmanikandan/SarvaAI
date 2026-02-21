package com.sarva.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GeneralAgentsTest {

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

    private ConversationMemory memory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        memory = new ConversationMemory();

        when(chatClientBuilder.defaultAdvisors(any(org.springframework.ai.chat.client.advisor.api.Advisor[].class)))
                .thenReturn(chatClientBuilder);
        when(chatClientBuilder.build()).thenReturn(chatClient);
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(anyString())).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
    }

    private static Stream<Arguments> agentProvider() {
        return Stream.of(
                Arguments.of("LAW", "What is ipc 302?"),
                Arguments.of("HEALTH", "Tips for headache"),
                Arguments.of("DEV", "How to use Spring Boot?"),
                Arguments.of("JAVA_INTERVIEW", "Explain HashMap"),
                Arguments.of("YOGA", "Mountain pose steps"),
                Arguments.of("MATRIMONY", "Match compatibility"),
                Arguments.of("EDU", "Learn English grammar"),
                Arguments.of("IOT", "Home automation sensors"),
                Arguments.of("COMMERCE", "Best laptop deals"),
                Arguments.of("GYM", "Chest workout"),
                Arguments.of("DIET", "Keto diet plan"),
                Arguments.of("SSLC", "Pythagoras theorem"),
                Arguments.of("TWELFTH", "Integration by parts"));
    }

    @ParameterizedTest
    @MethodSource("agentProvider")
    void testAgentHandling(String agentName, String query) {
        SarvaAgent agent = getAgentByName(agentName);

        when(vectorStore.similaritySearch(any(SearchRequest.class)))
                .thenReturn(Collections.singletonList(new Document("Mock context for " + agentName)));
        when(callResponseSpec.content()).thenReturn(agentName + " AI response");

        String response = agent.handle(query, memory);

        assertTrue(response.contains(agentName));
        assertEquals(agentName, agent.getName());
    }

    private SarvaAgent getAgentByName(String name) {
        return switch (name) {
            case "LAW" -> new LawAgent(chatClientBuilder, vectorStore);
            case "HEALTH" -> new HealthAgent(chatClientBuilder, vectorStore);
            case "DEV" -> new DevAgent(chatClientBuilder, vectorStore);
            case "JAVA_INTERVIEW" -> new JavaInterviewAgent(chatClientBuilder, vectorStore);
            case "YOGA" -> new YogaAgent(chatClientBuilder, vectorStore);
            case "MATRIMONY" -> new MatrimonyAgent(chatClientBuilder, vectorStore);
            case "EDU" -> new EduAgent(chatClientBuilder, vectorStore);
            case "IOT" -> new IoTAgent(chatClientBuilder, vectorStore);
            case "COMMERCE" -> new CommerceAgent(chatClientBuilder, vectorStore);
            case "GYM" -> new GymAgent(chatClientBuilder, vectorStore);
            case "DIET" -> new DietAgent(chatClientBuilder, vectorStore);
            case "SSLC" -> new SslcAgent(chatClientBuilder, vectorStore);
            case "TWELFTH" -> new TwelfthStandardAgent(chatClientBuilder, vectorStore);
            default -> throw new IllegalArgumentException("Unknown agent: " + name);
        };
    }
}
