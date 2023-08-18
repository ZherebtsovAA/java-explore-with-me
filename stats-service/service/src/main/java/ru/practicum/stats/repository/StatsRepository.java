package ru.practicum.stats.repository;

import ru.practicum.stats.dto.SearchCriteriaDto;
import ru.practicum.stats.model.Hit;
import ru.practicum.stats.model.ViewStats;

import java.util.List;

public interface StatsRepository {
    Hit save(Hit hit);

    List<ViewStats> findStats(SearchCriteriaDto searchCriteriaDto);
}