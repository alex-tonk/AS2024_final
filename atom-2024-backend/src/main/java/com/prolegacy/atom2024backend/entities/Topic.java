package com.prolegacy.atom2024backend.entities;

import com.prolegacy.atom2024backend.entities.ids.TopicId;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor
public class Topic {
    @Id
    @GeneratedValue(generator = "typed-sequence")
    private TopicId id;

    @Column(columnDefinition = "text")
    private String code;
    @Column(columnDefinition = "text")
    private String title;
    @Column(columnDefinition = "text")
    private String description;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "topic_lesson",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "lesson_id")
    )
    private List<Lesson> lessons = new ArrayList<>();
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "topic_trait",
            joinColumns = @JoinColumn(name = "topic_id"),
            inverseJoinColumns = @JoinColumn(name = "trait_id")
    )
    private List<Trait> traits = new ArrayList<>();

    public Topic(String code, String title, String description) {
        this.code = code;
        this.title = title;
        this.description = description;
    }
}
