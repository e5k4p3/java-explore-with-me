package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventRequestDto;
import ru.practicum.event.dto.EventResponseFullDto;
import ru.practicum.event.dto.EventResponseShortDto;
import ru.practicum.event.service.EventService;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.dto.RequestUpdateDto;
import ru.practicum.requests.dto.RequestUpdateResponseDto;
import ru.practicum.util.Create;
import ru.practicum.util.Update;
import ru.practicum.util.ValidationErrorsHandler;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {
    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventResponseShortDto> getAllEventsByUserId(@PathVariable Long userId) {
        log.debug("Получен GET запрос на получение события по id.");
        return eventService.getAllEventsByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponseFullDto addEvent(@PathVariable Long userId,
                                         @Validated(Create.class) @RequestBody EventRequestDto eventRequestDto,
                                         BindingResult bindingResult) {
        log.debug("Получен POST запрос на добавление события.");
        ValidationErrorsHandler.logValidationErrors(bindingResult);
        return eventService.addEvent(userId, eventRequestDto);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseFullDto getEventByUserIdAndEventId(@PathVariable Long userId,
                                                           @PathVariable Long eventId) {
        log.debug("Получен GET запрос на получение события по id.");
        return eventService.getEventByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseFullDto updateEventByUserIdAndEventId(@PathVariable Long userId,
                                                              @PathVariable Long eventId,
                                                              @Validated(Update.class) @RequestBody EventRequestDto eventRequestDto,
                                                              BindingResult bindingResult) {
        log.debug("Получен PATCH запрос на изменение события.");
        ValidationErrorsHandler.logValidationErrors(bindingResult);
        return eventService.updateEventByUserIdAndEventId(userId, eventId, eventRequestDto);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getRequestsByEventId(@PathVariable Long userId,
                                                 @PathVariable Long eventId) {
        log.debug("Получен GET запрос на получение запросов события.");
        return eventService.getRequestsByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public RequestUpdateResponseDto updateRequestsStatusByUserIdAndEventId(@PathVariable Long userId,
                                                                           @PathVariable Long eventId,
                                                                           @Valid @RequestBody RequestUpdateDto requestUpdateDto,
                                                                           BindingResult bindingResult) {
        log.debug("Получен PATCH запрос на изменение статуса запросов события.");
        ValidationErrorsHandler.logValidationErrors(bindingResult);
        return eventService.updateRequestsStatusByUserIdAndEventId(userId, eventId, requestUpdateDto);
    }
}
