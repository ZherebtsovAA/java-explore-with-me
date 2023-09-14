package ru.practicum.compilations.repository;

import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import ru.practicum.compilations.model.Compilations;

@UtilityClass
public class CompilationSpecifications {
    public Specification<Compilations> isPinned(Boolean pinned) {
        return (root, query, builder) -> builder.equal(root.get("pinned"), pinned);
    }
}