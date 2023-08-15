package ru.practicum.repository;

import dto.SearchCriteria;
import ru.practicum.model.Hit;
import ru.practicum.model.ViewStats;

import java.util.List;

public interface StatsRepository {
    Hit save(Hit hit);

    List<ViewStats> findStats(SearchCriteria searchCriteria);
}