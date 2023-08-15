package ru.practicum.service;

import dto.HitDto;
import dto.SearchCriteria;
import dto.ViewStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.repository.StatsRepository;

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
    public List<ViewStatsDto> findStats(SearchCriteria searchCriteria) {
        return StatsMapper.toViewStatsDto(statsRepository.findStats(searchCriteria));
    }
}