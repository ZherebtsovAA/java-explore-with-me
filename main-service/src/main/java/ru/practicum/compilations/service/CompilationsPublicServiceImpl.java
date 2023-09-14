package ru.practicum.compilations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.mapper.CompilationsMapper;
import ru.practicum.compilations.model.Compilations;
import ru.practicum.compilations.repository.CompilationSpecifications;
import ru.practicum.compilations.repository.CompilationsRepository;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.service.EventStatisticsService;
import ru.practicum.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationsPublicServiceImpl implements CompilationsPublicService {
    private final CompilationsRepository compilationsRepository;
    private final CompilationsMapper compilationsMapper;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventStatisticsService eventStatisticsService;

    @Override
    public List<CompilationDto> findAll(Boolean pinned, PageRequest pageRequest) {
        List<Compilations> foundCompilations;
        Specification<Compilations> searchCriteria = Specification
                .where(pinned == null ? null : CompilationSpecifications.isPinned(pinned));
        foundCompilations = compilationsRepository.findAll(searchCriteria, pageRequest).getContent();

        Set<Long> eventIds = foundCompilations.stream()
                .flatMap(f -> f.getEvents().stream())
                .collect(Collectors.toSet());

        List<Event> eventsByEventIds = eventRepository.findAllById(List.copyOf(eventIds));
        Map<Long, Event> eventIdToEvent = eventsByEventIds.stream()
                .collect(Collectors.toMap(Event::getId, event -> event));

        Map<Long, Integer> views = eventStatisticsService.getViews(eventsByEventIds);
        Map<Long, Integer> confirmedRequests = eventStatisticsService.getConfirmedRequests(eventsByEventIds);

        List<CompilationDto> listCompilationDto = new ArrayList<>(foundCompilations.size());
        for (Compilations compilation : foundCompilations) {

            List<EventShortDto> eventsShortDto = new ArrayList<>(compilation.getEvents().size());
            for (Long eventId : compilation.getEvents()) {
                Event event = eventIdToEvent.get(eventId);
                eventsShortDto.add(
                        eventMapper.toEventShortDto(
                                event,
                                confirmedRequests.getOrDefault(event.getId(), 0),
                                views.getOrDefault(event.getId(), 0)));
            }

            listCompilationDto.add(compilationsMapper.toCompilationDto(compilation, eventsShortDto));
        }

        return listCompilationDto;
    }

    @Override
    public CompilationDto findById(Long compId) {
        Compilations compilations = compilationsRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с id=" + compId + " не найдена"));

        List<Event> events = eventRepository.findAllById(List.copyOf(compilations.getEvents()));

        Map<Long, Integer> views = eventStatisticsService.getViews(events);
        Map<Long, Integer> confirmedRequests = eventStatisticsService.getConfirmedRequests(events);

        List<EventShortDto> eventsShortDto = new ArrayList<>(events.size());
        for (Event event : events) {
            eventsShortDto.add(
                    eventMapper.toEventShortDto(
                            event,
                            confirmedRequests.getOrDefault(event.getId(), 0),
                            views.getOrDefault(event.getId(), 0)));
        }

        return compilationsMapper.toCompilationDto(compilations, eventsShortDto);
    }
}