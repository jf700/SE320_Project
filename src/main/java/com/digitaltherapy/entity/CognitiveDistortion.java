package com.digitaltherapy.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "cognitive_distortions")
public class CognitiveDistortion {

    @Id
    private String id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "distortion_examples", joinColumns = @JoinColumn(name = "distortion_id"))
    @Column(name = "example")
    private List<String> examples;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getExamples() { return examples; }
    public void setExamples(List<String> examples) { this.examples = examples; }
}
