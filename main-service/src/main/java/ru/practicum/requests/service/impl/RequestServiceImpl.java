package ru.practicum.requests.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exceptions.ConflictOperationException;
import ru.practicum.exceptions.EntityAlreadyExistsException;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.exceptions.ForbiddenOperationException;
import ru.practicum.requests.dao.RequestRepository;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.service.RequestService;
import ru.practicum.requests.util.RequestMapper;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.event.model.enums.EventState.PUBLISHED;
import static ru.practicum.requests.model.enums.RequestStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public RequestDto addRequest(Long userId, Long eventId) {
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new EntityAlreadyExistsException("Запрос пользователя с id " + userId +
                    " на участие в событии с id " + eventId + " уже существует.");
        }
        User user = checkUserExistence(userId);
        Event event = checkEventExistence(eventId);
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictOperationException("Нельзя подать заявку на собственное событие.");
        }
        if (!event.getState().equals(PUBLISHED)) {
            throw new ConflictOperationException("Можно подать заявку только на опубликованное событие.");
        }
        Integer confirmedParticipants = event.getConfirmedRequests();
        if (confirmedParticipants >= event.getParticipantLimit()) {
            throw new ConflictOperationException("В событии уже участвует максимальное кол-во участников.");
        }
        Request request = new Request();
        request.setRequester(user);
        request.setEvent(event);
        request.setCreated(LocalDateTime.now().withNano(0));
        if (event.getRequestModeration()) {
            request.setStatus(PENDING);
        } else if (confirmedParticipants < event.getParticipantLimit() || event.getParticipantLimit().equals(0)) {
            request.setStatus(CONFIRMED);
            event.setConfirmedRequests(confirmedParticipants + 1);
        }

        log.info("Запрос пользователя с id " + userId + " на событие с id " + eventId + " было добавлено.");
        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) {
        checkUserExistence(userId);
        checkRequestIdExistence(requestId);
        Request request = requestRepository.findById(requestId).get();
        if (!request.getRequester().getId().equals(userId)) {
            throw new ForbiddenOperationException("Нельзя отменить не свой запрос на участие в событии.");
        }
        if (request.getStatus() == CONFIRMED) {
            Event event = checkEventExistence(request.getEvent().getId());
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
        }
        request.setStatus(CANCELED);

        log.info("Запрос с id " + requestId + " был отменен.");
        return RequestMapper.toRequestDto(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getAllRequestsByUserId(Long userId) {
        checkUserExistence(userId);
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    private void checkRequestIdExistence(Long requestId) {
        if (!requestRepository.existsById(requestId)) {
            throw new EntityNotFoundException("Запрос с id " + requestId + " не найден.");
        }
    }

    private User checkUserExistence(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с id " + userId + " не найден."));
    }

    private Event checkEventExistence(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("Событие с id " + eventId + " не найдено"));
    }
}
