package ru.practicum.event.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.StatsClient;
import ru.practicum.category.dao.CategoryRepository;
import ru.practicum.category.model.Category;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.dto.EventRequestDto;
import ru.practicum.event.dto.EventResponseFullDto;
import ru.practicum.event.dto.EventResponseShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.enums.EventSort;
import ru.practicum.event.model.enums.EventState;
import ru.practicum.event.model.enums.EventStateAction;
import ru.practicum.event.service.EventService;
import ru.practicum.event.util.EventMapper;
import ru.practicum.exceptions.ConflictOperationException;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.requests.dao.RequestRepository;
import ru.practicum.requests.dto.RequestDto;
import ru.practicum.requests.dto.RequestUpdateDto;
import ru.practicum.requests.dto.RequestUpdateResponseDto;
import ru.practicum.requests.model.Request;
import ru.practicum.requests.util.RequestMapper;
import ru.practicum.user.dao.UserRepository;
import ru.practicum.user.model.User;
import ru.practicum.util.LocalDateTimeFormatter;
import ru.practicum.util.ViewsSetter;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.event.model.enums.EventSort.EVENT_DATE;
import static ru.practicum.event.model.enums.EventSort.VIEWS;
import static ru.practicum.event.model.enums.EventState.*;
import static ru.practicum.requests.model.enums.RequestStatus.CONFIRMED;
import static ru.practicum.requests.model.enums.RequestStatus.REJECTED;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;
    private final ViewsSetter viewsSetter;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public EventResponseFullDto addEvent(Long userId, EventRequestDto eventRequestDto) {
        checkEventTime(eventRequestDto.getEventDate());
        Event event = EventMapper.toEvent(eventRequestDto);
        event.setCategory(checkCategoryExistence(eventRequestDto.getCategory()));
        event.setInitiator(checkUserExistence(userId));
        EventResponseFullDto eventResponseFullDto = EventMapper.toEventResponseFullDto(eventRepository.save(event));
        log.info("Событие с id " + eventResponseFullDto.getId() + " и названием " + eventResponseFullDto.getTitle() + " было добавлено.");
        return eventResponseFullDto;
    }

    @Override
    @Transactional
    public EventResponseFullDto updateEventByUserIdAndEventId(Long userId, Long eventId, EventRequestDto eventRequestDto) {
        checkUserExistence(userId);
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(
                () -> new EntityNotFoundException("Событие с id " + eventId + " не найдено."));
        if (event.getState() == PUBLISHED) {
            throw new ConflictOperationException("Нельзя изменить уже опубликованное событие.");
        }
        if (eventRequestDto.getStateAction() != null) {
            switch (eventRequestDto.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(CANCELED);
                    break;
            }
        }
        if (eventRequestDto.getEventDate() != null) {
            checkEventTime(eventRequestDto.getEventDate());
            event.setEventDate(LocalDateTimeFormatter.toLocalDateTime(eventRequestDto.getEventDate()));
        }
        patchCommonEventParts(eventRequestDto, event);

        log.info("Событие с id " + eventId + " было изменено.");
        return EventMapper.toEventResponseFullDto(event);
    }

    @Override
    @Transactional
    public EventResponseFullDto updateEventByIdByAdmin(Long eventId, EventRequestDto eventRequestDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("Событие с id " + eventId + " не найдено."));
        if (eventRequestDto.getEventDate() != null) {
            checkEventTimeForAdmin(eventRequestDto.getEventDate());
            event.setEventDate(LocalDateTimeFormatter.toLocalDateTime(eventRequestDto.getEventDate()));
        }
        patchCommonEventParts(eventRequestDto, event);
        if (eventRequestDto.getLocation() != null) {
            event.setLocation(eventRequestDto.getLocation());
        }
        if (eventRequestDto.getRequestModeration() != null) {
            event.setRequestModeration(eventRequestDto.getRequestModeration());
        }

        EventStateAction eventStateAction = eventRequestDto.getStateAction();
        EventState eventState = event.getState();

        if (eventRequestDto.getStateAction() != null) {
            switch (eventStateAction) {
                case PUBLISH_EVENT:
                    if (eventState == PENDING) {
                        event.setState(PUBLISHED);
                        event.setPublishedOn(LocalDateTime.now());
                    } else {
                        throw new ConflictOperationException("Событие можно опубликовать, только если оно находится в состоянии ожидания публикации.");
                    }
                    break;
                case REJECT_EVENT:
                    if (eventState != PUBLISHED) {
                        event.setState(CANCELED);
                    } else {
                        throw new ConflictOperationException("Событие можно отклонить, только если оно еще не опубликовано.");
                    }
                    break;
            }
        }

        log.info("Событие с id " + eventId + " было изменено.");
        return EventMapper.toEventResponseFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponseFullDto getEventById(Long eventId, HttpServletRequest request) {
        statsClient.addHit(request.getRequestURI(), request.getRemoteAddr());
        EventResponseFullDto event = EventMapper.toEventResponseFullDto(eventRepository.findById(eventId).orElseThrow(
                () -> new EntityNotFoundException("Событие с id " + eventId + " не найдено.")
        ));
        viewsSetter.setViewsToEventFullDto(event);
        return event;
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponseFullDto getEventByUserIdAndEventId(Long userId, Long eventId) {
        checkUserExistence(userId);
        EventResponseFullDto event = EventMapper.toEventResponseFullDto(eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(
                () -> new EntityNotFoundException("Событие с id " + eventId + " не найдено.")
        ));
        return viewsSetter.setViewsToEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseShortDto> getAllEventsByUserId(Long userId) {
        checkUserExistence(userId);
        List<EventResponseShortDto> events = eventRepository.findAllByInitiatorId(userId).stream()
                .map(EventMapper::toEventResponseShortDto)
                .collect(Collectors.toList());
        return viewsSetter.setViewsToEventsShortDto(events);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseShortDto> getAllEventsWithFilters(String text,
                                                               Set<Long> categories,
                                                               Boolean paid,
                                                               String rangeStart,
                                                               String rangeEnd,
                                                               Boolean onlyAvailable,
                                                               EventSort sort,
                                                               Integer from,
                                                               Integer size,
                                                               HttpServletRequest request) {
        statsClient.addHit(request.getRequestURI(), request.getRemoteAddr());

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> eventCriteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> eventRoot = eventCriteriaQuery.from(Event.class);
        eventCriteriaQuery.select(eventRoot);
        List<Predicate> predicates = new ArrayList<>();

        if (rangeStart != null && rangeEnd != null) {
            predicates.add(criteriaBuilder.between(eventRoot.get("eventDate"),
                    LocalDateTimeFormatter.toLocalDateTime(rangeStart),
                    LocalDateTimeFormatter.toLocalDateTime(rangeEnd)));
        } else {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(eventRoot.get("eventDate"), LocalDateTime.now().withNano(0)));
        }

        if (paid != null) {
            predicates.add(criteriaBuilder.equal(eventRoot.get("paid"), paid));
        }

        if (onlyAvailable) {
            predicates.add(criteriaBuilder.lessThan(eventRoot.get("confirmedRequests"), eventRoot.get("participantLimit")));
        }

        if (categories != null && !categories.isEmpty()) {
            Expression<Long> categoryIdExpression = eventRoot.get("category").get("id");
            predicates.add(categoryIdExpression.in(categories));
        }

        if (text != null) {
            predicates.add(criteriaBuilder.or(criteriaBuilder.like(criteriaBuilder.lower(eventRoot.get("annotation").as(String.class)),
                            criteriaBuilder.literal('%' + text.toLowerCase() + '%')),
                    criteriaBuilder.like(criteriaBuilder.lower(eventRoot.get("description").as(String.class)),
                            criteriaBuilder.literal('%' + text.toLowerCase() + '%'))));
        }

        eventCriteriaQuery.where(predicates.toArray(new Predicate[0]));

        if (sort.equals(EVENT_DATE)) {
            eventCriteriaQuery.orderBy(criteriaBuilder.desc(eventRoot.get("eventDate")));
        }

        TypedQuery<Event> eventTypedQuery = entityManager.createQuery(eventCriteriaQuery);
        PageRequest pageRequest = PageRequest.of((from / size), size);
        eventTypedQuery.setFirstResult(pageRequest.getPageNumber());
        eventTypedQuery.setMaxResults(pageRequest.getPageSize());
        List<Event> events = eventTypedQuery.getResultList();

        List<EventResponseShortDto> eventsShortDto = events.stream()
                .map(EventMapper::toEventResponseShortDto)
                .collect(Collectors.toList());

        viewsSetter.setViewsToEventsShortDto(eventsShortDto);

        if (sort.equals(VIEWS)) {
            return eventsShortDto.stream().sorted(Comparator.comparingLong(EventResponseShortDto::getViews)).collect(Collectors.toList());
        }

        return eventsShortDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponseFullDto> getAllEventsWithAdminFilters(Set<Long> users,
                                                                   Set<EventState> states,
                                                                   Set<Long> categories,
                                                                   String rangeStart,
                                                                   String rangeEnd,
                                                                   Integer from,
                                                                   Integer size) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> eventCriteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> eventRoot = eventCriteriaQuery.from(Event.class);
        eventCriteriaQuery.select(eventRoot);
        List<Predicate> predicates = new ArrayList<>();

        if (rangeStart != null && rangeEnd != null) {
            predicates.add(criteriaBuilder.between(eventRoot.get("eventDate"),
                    LocalDateTimeFormatter.toLocalDateTime(rangeStart),
                    LocalDateTimeFormatter.toLocalDateTime(rangeEnd)));
        }

        if (states != null && !states.isEmpty()) {
            Expression<EventState> eventStateExpression = eventRoot.get("state");
            predicates.add(eventStateExpression.in(states));
        }

        if (categories != null && !categories.isEmpty()) {
            Expression<Long> categoryIdExpression = eventRoot.get("category").get("id");
            predicates.add(categoryIdExpression.in(categories));
        }

        if (users != null && !users.isEmpty()) {
            Expression<Long> userIdExpression = eventRoot.get("initiator").get("id");
            predicates.add(userIdExpression.in(users));
        }

        eventCriteriaQuery.where(predicates.toArray(new Predicate[0]));
        TypedQuery<Event> eventTypedQuery = entityManager.createQuery(eventCriteriaQuery);
        PageRequest pageRequest = PageRequest.of((from / size), size);
        eventTypedQuery.setFirstResult(pageRequest.getPageNumber());
        eventTypedQuery.setMaxResults(pageRequest.getPageSize());
        List<Event> events = eventTypedQuery.getResultList();

        List<EventResponseFullDto> eventsFullDto = events.stream()
                .map(EventMapper::toEventResponseFullDto)
                .collect(Collectors.toList());

        viewsSetter.setViewsToEventsFullDto(eventsFullDto);

        return eventsFullDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getRequestsByUserIdAndEventId(Long userId, Long eventId) {
        checkUserExistence(userId);
        eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(
                () -> new EntityNotFoundException("Событие с id " + eventId + " не найдено."));
        return requestRepository.findAllByEventId(eventId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestUpdateResponseDto updateRequestsStatusByUserIdAndEventId(Long userId, Long eventId, RequestUpdateDto requestUpdateDto) {
        checkUserExistence(userId);
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(
                () -> new EntityNotFoundException("Событие с id " + eventId + " не найдено."));
        if (event.getParticipantLimit().equals(0) || !event.getRequestModeration()) {
            return new RequestUpdateResponseDto();
        }

        Integer confirmedParticipants = event.getConfirmedRequests();
        if (confirmedParticipants >= event.getParticipantLimit()) {
            throw new ConflictOperationException("В событии уже участвует макимальное кол-во участников.");
        }

        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();

        List<Request> requests = requestRepository.findAllByEventIdAndIdIn(eventId, requestUpdateDto.getRequestIds());
        Set<Long> requestsIds = requests.stream()
                .map(Request::getId)
                .collect(Collectors.toSet());
        requestUpdateDto.getRequestIds().forEach(l -> {
            if (!requestsIds.contains(l)) {
                throw new EntityNotFoundException("Заявка с id " + l + " не найдена.");
            }
        });

        switch (requestUpdateDto.getStatus()) {
            case CONFIRMED:
                for (Request request : requests) {
                    checkRequestStatus(request);
                    if (confirmedParticipants < event.getParticipantLimit()) {
                        request.setStatus(CONFIRMED);
                        confirmedRequests.add(request);
                        event.setConfirmedRequests(confirmedParticipants + 1);
                    } else {
                        request.setStatus(REJECTED);
                        rejectedRequests.add(request);
                    }
                }
                break;
            case REJECTED:
                for (Request request : requests) {
                    checkRequestStatus(request);
                    request.setStatus(REJECTED);
                    rejectedRequests.add(request);
                }
        }

        log.info("Статус запросов события с id " + eventId + " был изменен.");
        return new RequestUpdateResponseDto(confirmedRequests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList()),
                rejectedRequests.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList()));
    }

    private User checkUserExistence(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("Пользователь с id " + userId + " не найден.")
        );
    }

    private Category checkCategoryExistence(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new EntityNotFoundException("Категория с id " + categoryId + " не найдена.")
        );
    }

    private void checkEventTime(String eventTime) {
        if (LocalDateTimeFormatter.toLocalDateTime(eventTime).isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new ConflictOperationException("Дата начала события должны быть позже 2 часов от настоящего времени.");
        }
    }

    private void checkEventTimeForAdmin(String eventTime) {
        if (LocalDateTimeFormatter.toLocalDateTime(eventTime).isBefore(LocalDateTime.now().plusHours(1L))) {
            throw new ConflictOperationException("Дата начала события должна быть не ранее, чем за час от даты публикации.");
        }
    }

    private void patchCommonEventParts(EventRequestDto eventRequestDto, Event event) {
        if (eventRequestDto.getTitle() != null) {
            event.setTitle(eventRequestDto.getTitle());
        }
        if (eventRequestDto.getAnnotation() != null) {
            event.setAnnotation(eventRequestDto.getAnnotation());
        }
        if (eventRequestDto.getDescription() != null) {
            event.setDescription(eventRequestDto.getDescription());
        }
        if (eventRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventRequestDto.getParticipantLimit());
        }
        if (eventRequestDto.getCategory() != null) {
            event.setCategory(checkCategoryExistence(eventRequestDto.getCategory()));
        }
        if (eventRequestDto.getPaid() != null) {
            event.setPaid(eventRequestDto.getPaid());
        }
    }

    private void checkRequestStatus(Request request) {
        switch (request.getStatus()) {
            case CONFIRMED:
                throw new ConflictOperationException("Заявка с id " + request.getId() + " уже подтверждена.");
            case REJECTED:
                throw new ConflictOperationException("Заявка с id " + request.getId() + " уже отклонена.");
            case CANCELED:
                throw new ConflictOperationException("Заявка с id " + request.getId() + " уже отменена.");
        }
    }
}
