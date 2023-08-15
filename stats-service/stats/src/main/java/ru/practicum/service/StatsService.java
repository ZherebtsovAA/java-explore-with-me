package ru.practicum.service;

import dto.HitDto;
import dto.SearchCriteria;
import dto.ViewStatsDto;

import java.util.List;

public interface StatsService {
    void save(HitDto hitDto);

    List<ViewStatsDto> findStats(SearchCriteria searchCriteria);
}