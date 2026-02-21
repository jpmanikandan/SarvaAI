package com.sarva.core;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class JavaInterviewAgentManualTest {

    @Autowired
    private JavaInterviewAgent javaInterviewAgent;

    @Test
    public void testHandle() {
        String query = "What is the Collections Framework in Java?";
        ConversationMemory memory = new ConversationMemory();
        String response = javaInterviewAgent.handle(query, memory);
        assertNotNull(response);
        assertTrue(response.length() > 0);
        System.out.println("Response: " + response);
    }
}
