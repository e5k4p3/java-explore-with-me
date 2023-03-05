package ru.practicum.requests.service;

import ru.practicum.requests.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto addRequest(Long userId, Long eventId);

    List<RequestDto> getAllRequestsByUserId(Long userId);

    RequestDto cancelRequest(Long userId, Long requestId);
}
