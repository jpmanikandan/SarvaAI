package com.sarva.service;

import com.sarva.entity.ChatMessage;
import com.sarva.entity.ChatSession;
import com.sarva.repository.ChatMessageRepository;
import com.sarva.repository.ChatSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChatHistoryService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;

    public ChatHistoryService(ChatSessionRepository sessionRepository, 
                            ChatMessageRepository messageRepository) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
    }

    /**
     * Create a new chat session
     */
    public ChatSession createSession(String title) {
        ChatSession session = new ChatSession();
        session.setId(UUID.randomUUID().toString());
        session.setTitle(title);
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        return sessionRepository.save(session);
    }

    /**
     * Save a message to a session
     */
    @Transactional
    public ChatMessage saveMessage(String sessionId, String sender, String content, String expertName) {
        Optional<ChatSession> sessionOpt = sessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            throw new RuntimeException("Session not found: " + sessionId);
        }

        ChatSession session = sessionOpt.get();
        
        ChatMessage message = new ChatMessage();
        message.setSession(session);
        message.setSessionId(sessionId);
        message.setSender(sender);
        message.setContent(content);
        message.setExpertName(expertName);
        message.setTimestamp(LocalDateTime.now());

        // Update session's updatedAt timestamp
        session.setUpdatedAt(LocalDateTime.now());
        sessionRepository.save(session);

        return messageRepository.save(message);
    }

    /**
     * Get all sessions sorted by most recent
     */
    public List<ChatSession> getAllSessions() {
        return sessionRepository.findAllByOrderByUpdatedAtDesc();
    }

    /**
     * Get all messages for a session
     */
    public List<ChatMessage> getSessionMessages(String sessionId) {
        return messageRepository.findBySessionIdOrderByTimestampAsc(sessionId);
    }

    /**
     * Delete a session and all its messages
     */
    @Transactional
    public void deleteSession(String sessionId) {
        sessionRepository.deleteById(sessionId);
    }

    /**
     * Update session title
     */
    @Transactional
    public ChatSession updateSessionTitle(String sessionId, String title) {
        Optional<ChatSession> sessionOpt = sessionRepository.findById(sessionId);
        if (sessionOpt.isEmpty()) {
            throw new RuntimeException("Session not found: " + sessionId);
        }

        ChatSession session = sessionOpt.get();
        session.setTitle(title);
        session.setUpdatedAt(LocalDateTime.now());
        return sessionRepository.save(session);
    }

    /**
     * Get a session by ID
     */
    public Optional<ChatSession> getSession(String sessionId) {
        return sessionRepository.findById(sessionId);
    }
}
