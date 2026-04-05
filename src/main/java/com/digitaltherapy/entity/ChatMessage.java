package com.digitaltherapy.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_session_id")
    private UserSession userSession;

    @Enumerated(EnumType.STRING)
    private MessageRole role;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private Modality modality = Modality.TEXT;

    private LocalDateTime timestamp;

    public enum MessageRole { USER, ASSISTANT }
    public enum Modality { TEXT, VOICE, VIDEO }

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UserSession getUserSession() { return userSession; }
    public void setUserSession(UserSession userSession) { this.userSession = userSession; }
    public MessageRole getRole() { return role; }
    public void setRole(MessageRole role) { this.role = role; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Modality getModality() { return modality; }
    public void setModality(Modality modality) { this.modality = modality; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
