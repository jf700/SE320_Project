package com.digitaltherapy.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    private String name;

    private Boolean onboardingComplete = false;

    @Enumerated(EnumType.STRING)
    private OnboardingPath onboardingPath;

    @Enumerated(EnumType.STRING)
    private SeverityLevel severityLevel;

    private Integer streakDays = 0;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum OnboardingPath { SELF, THERAPIST_REFERRED }
    public enum SeverityLevel { MILD, MODERATE, SIGNIFICANT }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Boolean getOnboardingComplete() { return onboardingComplete; }
    public void setOnboardingComplete(Boolean onboardingComplete) { this.onboardingComplete = onboardingComplete; }
    public OnboardingPath getOnboardingPath() { return onboardingPath; }
    public void setOnboardingPath(OnboardingPath onboardingPath) { this.onboardingPath = onboardingPath; }
    public SeverityLevel getSeverityLevel() { return severityLevel; }
    public void setSeverityLevel(SeverityLevel severityLevel) { this.severityLevel = severityLevel; }
    public Integer getStreakDays() { return streakDays; }
    public void setStreakDays(Integer streakDays) { this.streakDays = streakDays; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
