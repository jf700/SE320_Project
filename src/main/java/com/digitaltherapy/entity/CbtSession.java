package com.digitaltherapy.entity;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cbt_sessions")
public class CbtSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private SessionModule module;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer durationMinutes;

    @ElementCollection
    @CollectionTable(name = "cbt_session_objectives", joinColumns = @JoinColumn(name = "session_id"))
    @Column(name = "objective")
    private List<String> objectives;

    @ElementCollection
    @CollectionTable(name = "cbt_session_modalities", joinColumns = @JoinColumn(name = "session_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "modality")
    private List<Modality> modalities;

    private Integer orderIndex;

    public enum Modality { TEXT, VOICE, VIDEO }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public SessionModule getModule() { return module; }
    public void setModule(SessionModule module) { this.module = module; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public List<String> getObjectives() { return objectives; }
    public void setObjectives(List<String> objectives) { this.objectives = objectives; }
    public List<Modality> getModalities() { return modalities; }
    public void setModalities(List<Modality> modalities) { this.modalities = modalities; }
    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
}
