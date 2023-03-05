package ru.practicum.event.util;

import lombok.experimental.UtilityClass;
import ru.practicum.category.util.CategoryMapper;
import ru.practicum.event.dto.EventRequestDto;
import ru.practicum.event.dto.EventResponseFullDto;
import ru.practicum.event.dto.EventResponseShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.user.util.UserMapper;
import ru.practicum.util.LocalDateTimeFormatter;

import java.time.LocalDateTime;

import static ru.practicum.event.model.enums.EventState.PENDING;

@UtilityClass
public final class EventMapper {
    public static EventResponseFullDto toEventResponseFullDto(Event event) {
        return new EventResponseFullDto(
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                LocalDateTimeFormatter.toString(event.getCreatedOn()),
                event.getDescription(),
                LocalDateTimeFormatter.toString(event.getEventDate()),
                event.getId(),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getLocation(),
                event.getPaid(),
                event.getParticipantLimit(),
                LocalDateTimeFormatter.toString(event.getPublishedOn()),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                0L
        );
    }

    public static EventResponseShortDto toEventResponseShortDto(Event event) {
        return new EventResponseShortDto(
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                LocalDateTimeFormatter.toString(event.getEventDate()),
                event.getId(),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                0L
        );
    }

    public static Event toEvent(EventRequestDto eventRequestDto) {
        return new Event(
                null,
                eventRequestDto.getTitle(),
                eventRequestDto.getAnnotation(),
                eventRequestDto.getDescription(),
                eventRequestDto.getParticipantLimit(),
                LocalDateTimeFormatter.toLocalDateTime(eventRequestDto.getEventDate()),
                LocalDateTime.now(),
                null,
                0,
                null,
                null,
                eventRequestDto.getLocation(),
                eventRequestDto.getPaid(),
                eventRequestDto.getRequestModeration(),
                PENDING
        );
    }
}
