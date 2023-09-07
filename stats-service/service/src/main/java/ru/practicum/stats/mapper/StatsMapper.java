package ru.practicum.stats.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.stats.model.ViewStats;
import ru.practicum.stats.HitDto;
import ru.practicum.stats.ViewStatsDto;
import ru.practicum.stats.model.Hit;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class StatsMapper {
    public Hit toHit(HitDto hitDto) {
        return new Hit(null, hitDto.getApp(), hitDto.getUri(), hitDto.getIp(), hitDto.getTimestamp());
    }

    public ViewStatsDto toViewStatsDto(ViewStats viewStats) {
        return new ViewStatsDto(viewStats.getApp(), viewStats.getUri(), viewStats.getHits());
    }

    public List<ViewStatsDto> toViewStatsDto(Iterable<ViewStats> viewStats) {
        List<ViewStatsDto> result = new ArrayList<>();
        for (ViewStats element : viewStats) {
            result.add(toViewStatsDto(element));
        }

        return result;
    }
}