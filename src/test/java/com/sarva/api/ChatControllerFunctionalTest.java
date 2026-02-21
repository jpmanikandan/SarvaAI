package com.sarva.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sarva.core.AgentRouter;
import com.sarva.core.ConversationMemory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for functional api test
public class ChatControllerFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AgentRouter agentRouter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testChatEndpoint() throws Exception {
        ChatController.ChatRequest request = new ChatController.ChatRequest("Hello", "English", "English");
        
        when(agentRouter.route(eq("Hello"), eq("English"), eq("English"), any(ConversationMemory.class)))
            .thenReturn("[AGENT:GENERAL] Hi there!");

        mockMvc.perform(post("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .session(new MockHttpSession()))
                .andExpect(status().isOk())
                .andExpect(content().string("[AGENT:GENERAL] Hi there!"));
    }
}
