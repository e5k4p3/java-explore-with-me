package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventRequestDto;
import ru.practicum.event.dto.EventResponseFullDto;
import ru.practicum.event.model.enums.EventState;
import ru.practicum.event.service.EventService;

import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventResponseFullDto> getAllEventsByAdminFilters(@RequestParam(required = false) Set<Long> users,
                                                                 @RequestParam(required = false) Set<EventState> states,
                                                                 @RequestParam(required = false) Set<Long> categories,
                                                                 @RequestParam(required = false) String rangeStart,
                                                                 @RequestParam(required = false) String rangeEnd,
                                                                 @RequestParam(defaultValue = "0") Integer from,
                                                                 @RequestParam(defaultValue = "10") Integer size) {
        log.debug("[ADMIN] Получен GET запрос на получение событий по фильтрам.");
        return eventService.getAllEventsWithAdminFilters(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseFullDto updateEventByIdByAdmin(@PathVariable Long eventId,
                                                       @RequestBody EventRequestDto eventRequestDto) {
        log.debug("[ADMIN] Получен PATCH запрос на изменение события.");
        return eventService.updateEventByIdByAdmin(eventId, eventRequestDto);
    }
}
