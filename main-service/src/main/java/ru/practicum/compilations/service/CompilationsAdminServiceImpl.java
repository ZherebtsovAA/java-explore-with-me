package ru.practicum.compilations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.mapper.CompilationsMapper;
import ru.practicum.compilations.model.Compilations;
import ru.practicum.compilations.model.UpdateCompilationRequest;
import ru.practicum.compilations.repository.CompilationsRepository;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.service.EventStatisticsService;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;

import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationsAdminServiceImpl implements CompilationsAdminService {
    private final CompilationsRepository compilationsRepository;
    private final CompilationsMapper compilationsMapper;
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final EventStatisticsService eventStatisticsService;

    @Transactional
    @Override
    public CompilationDto save(NewCompilationDto newCompilationDto) {
        Compilations compilations = compilationsRepository.save(compilationsMapper.toCompilations(newCompilationDto));

        if (newCompilationDto.getEvents() == null || newCompilationDto.getEvents().isEmpty()) {
            return compilationsMapper.toCompilationDto(compilations, Collections.emptyList());
        }

        List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());

        Map<Long, Integer> views = eventStatisticsService.getViews(events);
        Map<Long, Integer> confirmedRequests = eventStatisticsService.getConfirmedRequests(events);

        List<EventShortDto> eventsShortDto = new ArrayList<>(events.size());
        for (Event event : events) {
            eventsShortDto.add(eventMapper.toEventShortDto(
                    event,
                    confirmedRequests.getOrDefault(event.getId(), 0),
                    views.getOrDefault(event.getId(), 0)));
        }

        return compilationsMapper.toCompilationDto(compilations, eventsShortDto);
    }

    @Transactional
    @Override
    public void deleteById(Long compId) {
        Compilations compilations = compilationsRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с id=" + compId + " не найдена"));

        compilationsRepository.delete(compilations);
    }

    @Transactional
    @Override
    public CompilationDto patchUpdate(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilations compilations = compilationsRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка с id=" + compId + " не найдена"));

        compilationsRepository.save(checkBeforeUpdate(compilations, updateCompilationRequest));

        List<Event> events = eventRepository.findAllById(List.copyOf(compilations.getEvents()));

        Map<Long, Integer> views = eventStatisticsService.getViews(events);
        Map<Long, Integer> confirmedRequests = eventStatisticsService.getConfirmedRequests(events);

        List<EventShortDto> eventsShortDto = new ArrayList<>(events.size());
        for (Event event : events) {
            eventsShortDto.add(eventMapper.toEventShortDto(
                    event,
                    confirmedRequests.getOrDefault(event.getId(), 0),
                    views.getOrDefault(event.getId(), 0)));
        }

        return compilationsMapper.toCompilationDto(compilations, eventsShortDto);
    }

    private Compilations checkBeforeUpdate(Compilations compilations, UpdateCompilationRequest updateCompilationRequest) {
        Set<Long> eventsId = updateCompilationRequest.getEvents();
        if (eventsId != null) {
            if (!eventsId.isEmpty()) {
                compilations.setEvents(eventsId);
            }
        }

        Boolean pinned = updateCompilationRequest.getPinned();
        if (pinned != null) {
            compilations.setPinned(pinned);
        }

        String title = updateCompilationRequest.getTitle();
        if (title != null) {
            if (title.length() < 1 || title.length() > 50 || title.isBlank()) {
                throw new BadRequestException("Длина поля title должна быть: min = 1, max = 50");
            }
            compilations.setTitle(title);
        }

        return compilations;
    }
}