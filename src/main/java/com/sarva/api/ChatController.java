
package com.sarva.api;

import com.sarva.core.AgentRouter;
import com.sarva.core.ConversationMemory;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final AgentRouter agentRouter;
    private static final String MEMORY_KEY = "conversationMemory";

    public ChatController(AgentRouter agentRouter) {
        this.agentRouter = agentRouter;
    }

    public record ChatRequest(String query, String inputLanguage, String outputLanguage) {
    }

    @PostMapping
    public String chat(@RequestBody ChatRequest request, HttpSession session) {
        try {
            // Get or create conversation memory for this session
            ConversationMemory memory = (ConversationMemory) session.getAttribute(MEMORY_KEY);
            if (memory == null) {
                memory = new ConversationMemory();
                session.setAttribute(MEMORY_KEY, memory);
            }

            // Route query with conversation context
            String response = agentRouter.route(request.query(), request.inputLanguage(),
                    request.outputLanguage(), memory);

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatStream(@RequestBody ChatRequest request, HttpSession session) {
        try {
            // Get or create conversation memory for this session
            ConversationMemory memory = (ConversationMemory) session.getAttribute(MEMORY_KEY);
            if (memory == null) {
                memory = new ConversationMemory();
                session.setAttribute(MEMORY_KEY, memory);
            }

            // Route query with streaming response
            return agentRouter.routeStream(request.query(), request.inputLanguage(),
                    request.outputLanguage(), memory)
                    .map(chunk -> ServerSentEvent.builder(chunk).build());
        } catch (Exception e) {
            e.printStackTrace();
            return Flux.just(ServerSentEvent.builder("Error: " + e.getMessage()).build());
        }
    }

    @GetMapping(value = "/test-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> testStream() {
        return Flux.just("Hello", "World", "This", "Is", "A", "Test")
                .delayElements(java.time.Duration.ofMillis(500))
                .map(word -> ServerSentEvent.builder(word).build());
    }
}
