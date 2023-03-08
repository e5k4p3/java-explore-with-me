package ru.practicum.requests.util;

import lombok.experimental.UtilityClass;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.model.Request;

@UtilityClass
public final class RequestMapper {
    public static RequestDto toRequestDto(Request request) {
        return new RequestDto(
                request.getId(),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getCreated(),
                request.getStatus()
        );
    }
}
