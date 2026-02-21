package com.sarva.repository;

import com.sarva.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, String> {
    
    List<ChatSession> findAllByOrderByUpdatedAtDesc();
    
    // For future multi-user support
    List<ChatSession> findByUserIdOrderByUpdatedAtDesc(String userId);
}
