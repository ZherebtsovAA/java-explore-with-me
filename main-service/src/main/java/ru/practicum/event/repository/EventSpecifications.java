package ru.practicum.event.repository;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.user.model.User;

import javax.persistence.criteria.Join;
import java.time.LocalDateTime;
import java.util.List;


@UtilityClass
public class EventSpecifications {
    public Specification<Event> belongsToInitiators(List<Long> users) {
        return (root, query, builder) -> {
            Join<User, Event> eventUser = root.join("initiator");
            return builder.in(eventUser.get("id")).value(users);
        };
    }

    public Specification<Event> belongsToStates(List<EventState> states) {
        return (root, query, builder) -> builder.in(root.get("state")).value(states);
    }

    public Specification<Event> belongsToCategories(List<Long> categories) {
        return (root, query, builder) -> {
            Join<Category, Event> eventCategory = root.join("category");
            return builder.in(eventCategory.get("id")).value(categories);
        };
    }

    public Specification<Event> eventDateGreaterThan(LocalDateTime rangeStart) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart);
    }

    public Specification<Event> eventDatelessThan(LocalDateTime rangeEnd) {
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd);
    }

    public Specification<Event> belongsToId(Long eventId) {
        return (root, query, builder) -> builder.equal(root.get("id"), eventId);
    }

    public Specification<Event> containsToAnnotationCaseInsensitive(String annotation) {
        return (root, query, builder) -> builder.like(builder.upper(root.get("annotation")), "%" + annotation.toUpperCase() + "%");
        //return (root, query, builder) -> builder.like(root.get("annotation"), "%" + text.toUpperCase() + "%");
    }

    public Specification<Event> containsToDescriptionCaseInsensitive(String description) {
        return (root, query, builder) -> builder.like(builder.upper(root.get("description")), "%" + description.toUpperCase() + "%");
        //return (root, query, builder) -> builder.like(root.get("description"), "%" + text.toUpperCase() + "%");
    }

    public Specification<Event> isPaid(Boolean paid) {
        return (root, query, builder) -> builder.equal(root.get("paid"), paid);
    }
}