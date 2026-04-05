package com.digitaltherapy.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_sessions")
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cbt_session_id")
    private CbtSession cbtSession;

    @Enumerated(EnumType.STRING)
    private SessionStatus status = SessionStatus.IN_PROGRESS;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private Integer moodBefore;
    private Integer moodAfter;

    public enum SessionStatus { IN_PROGRESS, COMPLETED, EARLY_EXIT }

    @PrePersist
    protected void onCreate() {
        startedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public CbtSession getCbtSession() { return cbtSession; }
    public void setCbtSession(CbtSession cbtSession) { this.cbtSession = cbtSession; }
    public SessionStatus getStatus() { return status; }
    public void setStatus(SessionStatus status) { this.status = status; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }
    public Integer getMoodBefore() { return moodBefore; }
    public void setMoodBefore(Integer moodBefore) { this.moodBefore = moodBefore; }
    public Integer getMoodAfter() { return moodAfter; }
    public void setMoodAfter(Integer moodAfter) { this.moodAfter = moodAfter; }
}
