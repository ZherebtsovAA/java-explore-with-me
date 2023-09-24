package ru.practicum.event.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.category.model.Category;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 2000)
    private String annotation;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "category")
    private Category category;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "initiator")
    private User initiator;
    @CreationTimestamp
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;
    @Column(nullable = false, length = 7000)
    private String description;
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
    @Column(nullable = false)
    private Float lat;
    @Column(nullable = false)
    private Float lon;
    @Column(columnDefinition = "boolean default false")
    private Boolean paid;
    @Column(name = "participant_limit", columnDefinition = "integer default 0")
    private Integer participantLimit;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation", columnDefinition = "boolean default true")
    private Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventState state;
    @Column(nullable = false, length = 120)
    private String title;
    @ElementCollection
    @CollectionTable(name = "admin_comments_event", joinColumns = @JoinColumn(name = "event_id"))
    @AttributeOverrides({
            @AttributeOverride(name = "comment", column = @Column(name = "comment")),
            @AttributeOverride(name = "createdOn", column = @Column(name = "comment_created")),
    })
    @ToString.Exclude
    private List<AdminComment> adminComments;

    public Location getLocation() {
        return new Location(lat, lon);
    }
}