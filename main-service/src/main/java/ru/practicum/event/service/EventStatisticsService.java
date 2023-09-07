package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.request.repository.IConfirmedRequests;
import ru.practicum.request.repository.RequestParticipationRepository;
import ru.practicum.stats.HitDto;
import ru.practicum.stats.StatsClient;
import ru.practicum.stats.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.request.model.RequestParticipationState.CONFIRMED;

@Service
@RequiredArgsConstructor
public class EventStatisticsService {
    private final StatsClient statsClient;
    private final RequestParticipationRepository requestParticipationRepository;

    public void saveStats(String uri, String ip) {
        statsClient.saveStats(new HitDto("main-service", uri, ip, LocalDateTime.now()));
    }

    // return Map(key - eventId, value - count views)
    public Map<Long, Integer> getViews(List<Event> events) {
        if (events == null || events.isEmpty()) {
            return new HashMap<>(0);
        }

        LocalDateTime start = events.stream()
                .map(Event::getCreatedOn)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.of(1900, 1, 1, 0, 0, 0));

        LocalDateTime end = LocalDateTime.now();

        List<String> uris = events.stream()
                .map(event -> String.join("/", "/events", event.getId().toString()))
                .collect(Collectors.toList());

        boolean unique = true;

        List<ViewStatsDto> statistics = statsClient.getStats(start, end, uris, unique);
        if (statistics.isEmpty()) {
            return new HashMap<>(0);
        }

        return statistics.stream()
                .collect(Collectors.toMap(x -> Long.parseLong(x.getUri().replace("/events/", "")), ViewStatsDto::getHits));
    }

    public Map<Long, Integer> getConfirmedRequests(List<Event> events) {
        if (events == null || events.isEmpty()) {
            return new HashMap<>();
        }

        return requestParticipationRepository.findByStatusAndEventIn(CONFIRMED, events).stream()
                .collect(Collectors.toMap(IConfirmedRequests::getEventId, IConfirmedRequests::getTotalRequest));
    }
}