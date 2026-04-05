package com.digitaltherapy.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "diary_entries")
public class DiaryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String situation;

    @Column(columnDefinition = "TEXT")
    private String automaticThought;

    @ElementCollection
    @CollectionTable(name = "diary_entry_emotions", joinColumns = @JoinColumn(name = "entry_id"))
    @Column(name = "emotion")
    private List<String> emotions;

    @ManyToMany
    @JoinTable(
        name = "diary_entry_distortions",
        joinColumns = @JoinColumn(name = "entry_id"),
        inverseJoinColumns = @JoinColumn(name = "distortion_id")
    )
    private List<CognitiveDistortion> distortions;

    @Column(columnDefinition = "TEXT")
    private String alternativeThought;

    private Integer moodBefore;
    private Integer moodAfter;
    private Integer beliefRatingBefore;
    private Integer beliefRatingAfter;

    private LocalDateTime createdAt;

    private Boolean deleted = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getSituation() { return situation; }
    public void setSituation(String situation) { this.situation = situation; }
    public String getAutomaticThought() { return automaticThought; }
    public void setAutomaticThought(String automaticThought) { this.automaticThought = automaticThought; }
    public List<String> getEmotions() { return emotions; }
    public void setEmotions(List<String> emotions) { this.emotions = emotions; }
    public List<CognitiveDistortion> getDistortions() { return distortions; }
    public void setDistortions(List<CognitiveDistortion> distortions) { this.distortions = distortions; }
    public String getAlternativeThought() { return alternativeThought; }
    public void setAlternativeThought(String alternativeThought) { this.alternativeThought = alternativeThought; }
    public Integer getMoodBefore() { return moodBefore; }
    public void setMoodBefore(Integer moodBefore) { this.moodBefore = moodBefore; }
    public Integer getMoodAfter() { return moodAfter; }
    public void setMoodAfter(Integer moodAfter) { this.moodAfter = moodAfter; }
    public Integer getBeliefRatingBefore() { return beliefRatingBefore; }
    public void setBeliefRatingBefore(Integer beliefRatingBefore) { this.beliefRatingBefore = beliefRatingBefore; }
    public Integer getBeliefRatingAfter() { return beliefRatingAfter; }
    public void setBeliefRatingAfter(Integer beliefRatingAfter) { this.beliefRatingAfter = beliefRatingAfter; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Boolean getDeleted() { return deleted; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
}
