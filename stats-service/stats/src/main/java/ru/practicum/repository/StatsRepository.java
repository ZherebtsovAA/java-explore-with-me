package ru.practicum.repository;

import ru.practicum.dto.SearchCriteria;
import ru.practicum.model.Hit;
import ru.practicum.dto.ViewStats;

import java.util.List;

public interface StatsRepository {
    Hit save(Hit hit);

    List<ViewStats> findStats(SearchCriteria searchCriteria);
}