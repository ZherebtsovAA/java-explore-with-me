package ru.practicum.compilations.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "compilations")
public class Compilations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean pinned;
    @Column(nullable = false, length = 50)
    private String title;
    @ElementCollection
    @CollectionTable(name="compilations_events", joinColumns = @JoinColumn(name = "compilations_id"))
    @Column(name = "event_id")
    private Set<Long> events;
}