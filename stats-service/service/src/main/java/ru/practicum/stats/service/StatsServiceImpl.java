package ru.practicum.stats.service;

import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.SearchCriteriaDto;
import ru.practicum.stats.dto.ViewStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.mapper.StatsMapper;
import ru.practicum.stats.repository.StatsRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Transactional
    @Override
    public void save(HitDto hitDto) {
        statsRepository.save(StatsMapper.toHit(hitDto));
    }

    @Override
    public List<ViewStatsDto> findStats(SearchCriteriaDto searchCriteriaDto) {
        return StatsMapper.toViewStatsDto(statsRepository.findStats(searchCriteriaDto));
    }
}