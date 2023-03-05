package ru.practicum.event.service;

import ru.practicum.event.dto.EventRequestDto;
import ru.practicum.event.dto.EventResponseFullDto;
import ru.practicum.event.dto.EventResponseShortDto;
import ru.practicum.event.model.enums.EventSort;
import ru.practicum.event.model.enums.EventState;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.dto.RequestUpdateDto;
import ru.practicum.requests.dto.RequestUpdateResponseDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

public interface EventService {
    EventResponseFullDto addEvent(Long userId, EventRequestDto eventRequestDto);

    List<EventResponseShortDto> getAllEventsByUserId(Long userId);

    EventResponseFullDto getEventByUserIdAndEventId(Long userId, Long eventId);

    EventResponseFullDto updateEventByUserIdAndEventId(Long userId, Long eventId, EventRequestDto eventRequestDto);

    EventResponseFullDto getEventById(Long eventId, HttpServletRequest request);

    List<EventResponseShortDto> getAllEventsWithFilters(String text,
                                                        Set<Long> categories,
                                                        Boolean paid,
                                                        String rangeStart,
                                                        String rangeEnd,
                                                        Boolean onlyAvailable,
                                                        EventSort sort,
                                                        Integer from,
                                                        Integer size,
                                                        HttpServletRequest request);

    List<EventResponseFullDto> getAllEventsWithAdminFilters(Set<Long> users,
                                                            Set<EventState> states,
                                                            Set<Long> categories,
                                                            String rangeStart,
                                                            String rangeEnd,
                                                            Integer from,
                                                            Integer size);

    EventResponseFullDto updateEventByIdByAdmin(Long eventId, EventRequestDto eventRequestDto);

    List<RequestDto> getRequestsByUserIdAndEventId(Long userId, Long eventId);

    RequestUpdateResponseDto updateRequestsStatusByUserIdAndEventId(Long userId, Long eventId, RequestUpdateDto requestUpdateDto);
}
