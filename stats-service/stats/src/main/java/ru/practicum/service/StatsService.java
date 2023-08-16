package ru.practicum.service;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.SearchCriteria;
import ru.practicum.dto.ViewStatsDto;

import java.util.List;

public interface StatsService {
    void save(HitDto hitDto);

    List<ViewStatsDto> findStats(SearchCriteria searchCriteria);
}