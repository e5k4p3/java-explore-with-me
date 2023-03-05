package ru.practicum.event.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.event.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorId(Long userId);

    Optional<Event> findByInitiatorIdAndId(Long userId, Long eventId);

    List<Event> findAllByIdIn(List<Long> ids);

    Boolean existsByCategoryId(Long categoryId);
}
