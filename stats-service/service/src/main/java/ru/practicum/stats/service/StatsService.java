package ru.practicum.stats.service;

import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.SearchCriteriaDto;
import ru.practicum.stats.dto.ViewStatsDto;

import java.util.List;

public interface StatsService {
    void save(HitDto hitDto);

    List<ViewStatsDto> findStats(SearchCriteriaDto searchCriteriaDto);
}