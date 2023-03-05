package ru.practicum.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.StatsClient;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.event.dto.EventResponseFullDto;
import ru.practicum.event.dto.EventResponseShortDto;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ViewsSetter {
    private final StatsClient statsClient;

    public List<EventResponseFullDto> setViewsToEventsFullDto(List<EventResponseFullDto> eventsFullDto) {
        Set<String> uris = new HashSet<>();
        for (EventResponseFullDto eventFullDto : eventsFullDto) {
            uris.add("/events/" + eventFullDto.getId());
        }

        Map<Long, Long> views = getViewsForEvents(uris);

        eventsFullDto.forEach(e -> e.setViews(views.get(e.getId())));

        return eventsFullDto;
    }

    public List<EventResponseShortDto> setViewsToEventsShortDto(List<EventResponseShortDto> eventsShortDto) {
        Set<String> uris = new HashSet<>();
        for (EventResponseShortDto eventShortDto : eventsShortDto) {
            uris.add("/events/" + eventShortDto.getId());
        }

        Map<Long, Long> views = getViewsForEvents(uris);

        eventsShortDto.forEach(e -> e.setViews(views.get(e.getId())));

        return eventsShortDto;
    }

    public EventResponseFullDto setViewsToEventFullDto(EventResponseFullDto eventFullDto) {
        List<StatsResponseDto> stats = getViewsForEvent(eventFullDto.getId());

        if (!stats.isEmpty()) {
            eventFullDto.setViews(stats.get(0).getHits());
        }

        return eventFullDto;
    }

    public EventResponseShortDto setViewsToEventShortDto(EventResponseShortDto eventShortDto) {
        List<StatsResponseDto> stats = getViewsForEvent(eventShortDto.getId());

        if (!stats.isEmpty()) {
            eventShortDto.setViews(stats.get(0).getHits());
        }

        return eventShortDto;
    }

    private Map<Long, Long> getViewsForEvents(Set<String> uris) {
        List<StatsResponseDto> stats = statsClient.getAllStats(uris);

        Map<Long, Long> views = new HashMap<>();

        for (StatsResponseDto stat : stats) {
            views.put(
                    Long.parseLong(stat.getUri().replace("/events/", "")),
                    stat.getHits()
            );
        }

        return views;
    }


    private List<StatsResponseDto> getViewsForEvent(Long eventId) {
        Set<String> uri = new HashSet<>();
        uri.add("/events/" + eventId);
        return statsClient.getAllStats(uri);
    }


}
