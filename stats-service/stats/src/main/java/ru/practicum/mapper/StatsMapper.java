package ru.practicum.mapper;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.ViewStatsDto;
import lombok.NoArgsConstructor;
import ru.practicum.model.Hit;
import ru.practicum.dto.ViewStats;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class StatsMapper {
    public static Hit toHit(HitDto hitDto) {
        return new Hit(null, hitDto.getApp(), hitDto.getUri(), hitDto.getIp(), hitDto.getTimestamp());
    }

    public static ViewStatsDto toViewStatsDto(ViewStats viewStats) {
        return new ViewStatsDto(viewStats.getApp(), viewStats.getUri(), viewStats.getHits());
    }

    public static List<ViewStatsDto> toViewStatsDto(Iterable<ViewStats> viewStats) {
        List<ViewStatsDto> result = new ArrayList<>();
        for (ViewStats element : viewStats) {
            result.add(toViewStatsDto(element));
        }

        return result;
    }
}