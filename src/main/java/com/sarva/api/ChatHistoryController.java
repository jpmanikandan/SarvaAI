package com.sarva.api;

import com.sarva.entity.ChatMessage;
import com.sarva.entity.ChatSession;
import com.sarva.service.ChatHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
public class ChatHistoryController {

    private final ChatHistoryService chatHistoryService;

    public ChatHistoryController(ChatHistoryService chatHistoryService) {
        this.chatHistoryService = chatHistoryService;
    }

    /**
     * Create a new chat session
     * POST /api/history/sessions
     */
    @PostMapping("/sessions")
    public ResponseEntity<ChatSession> createSession(@RequestBody Map<String, String> request) {
        String title = request.get("title");
        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        ChatSession session = chatHistoryService.createSession(title);
        return ResponseEntity.ok(session);
    }

    /**
     * Get all chat sessions
     * GET /api/history/sessions
     */
    @GetMapping("/sessions")
    public ResponseEntity<List<ChatSession>> getAllSessions() {
        List<ChatSession> sessions = chatHistoryService.getAllSessions();
        return ResponseEntity.ok(sessions);
    }

    /**
     * Get a specific session
     * GET /api/history/sessions/{id}
     */
    @GetMapping("/sessions/{id}")
    public ResponseEntity<ChatSession> getSession(@PathVariable String id) {
        return chatHistoryService.getSession(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all messages for a session
     * GET /api/history/sessions/{id}/messages
     */
    @GetMapping("/sessions/{id}/messages")
    public ResponseEntity<List<ChatMessage>> getSessionMessages(@PathVariable String id) {
        List<ChatMessage> messages = chatHistoryService.getSessionMessages(id);
        return ResponseEntity.ok(messages);
    }

    /**
     * Save a message to a session
     * POST /api/history/sessions/{id}/messages
     */
    @PostMapping("/sessions/{id}/messages")
    public ResponseEntity<ChatMessage> saveMessage(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        
        String sender = request.get("sender");
        String content = request.get("content");
        String expertName = request.get("expertName");

        if (sender == null || content == null) {
            return ResponseEntity.badRequest().build();
        }

        try {
            ChatMessage message = chatHistoryService.saveMessage(id, sender, content, expertName);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update session title
     * PUT /api/history/sessions/{id}
     */
    @PutMapping("/sessions/{id}")
    public ResponseEntity<ChatSession> updateSessionTitle(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        
        String title = request.get("title");
        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            ChatSession session = chatHistoryService.updateSessionTitle(id, title);
            return ResponseEntity.ok(session);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a session
     * DELETE /api/history/sessions/{id}
     */
    @DeleteMapping("/sessions/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable String id) {
        chatHistoryService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }
}
