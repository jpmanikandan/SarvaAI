package com.sarva.core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.chat.client.ChatClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class DomainClassifierTest {

    private DomainClassifier domainClassifier;

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;

    @Mock
    private ChatClient.CallResponseSpec callResponseSpec;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        domainClassifier = new DomainClassifier(chatClient);

        // Setup fluent API mocking for ChatClient
        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.system(anyString())).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);
    }

    @Test
    void testClassifyLaw() {
        when(callResponseSpec.content()).thenReturn("LAW");
        String result = domainClassifier.classify("What are the laws for property?");
        assertEquals("LAW", result);
    }

    @Test
    void testClassifyFinance() {
        when(callResponseSpec.content()).thenReturn("FINANCE");
        String result = domainClassifier.classify("What is the current gold rate?");
        assertEquals("FINANCE", result);
    }

    @Test
    void testClassifyJavaInterview() {
        when(callResponseSpec.content()).thenReturn("JAVA_INTERVIEW");
        String result = domainClassifier.classify("Explain Spring Boot annotations.");
        assertEquals("JAVA_INTERVIEW", result);
    }

    @Test
    void testClassifyGeneralFallBack() {
        when(callResponseSpec.content()).thenReturn("I am not sure");
        String result = domainClassifier.classify("Random query");
        assertEquals("GENERAL", result);
    }
}
