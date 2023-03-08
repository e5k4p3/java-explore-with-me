package ru.practicum.compilations.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilations.dao.CompilationRepository;
import ru.practicum.compilations.dto.CompilationRequestDto;
import ru.practicum.compilations.dto.CompilationResponseDto;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.compilations.service.CompilationService;
import ru.practicum.compilations.util.CompilationMapper;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.event.model.Event;
import ru.practicum.exceptions.ConflictOperationException;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.util.ViewsSetter;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final ViewsSetter viewsSetter;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public CompilationResponseDto addCompilation(CompilationRequestDto compilationRequestDto) {
        checkTitleExistence(compilationRequestDto.getTitle());
        List<Event> events = eventRepository.findAllByIdIn(compilationRequestDto.getEvents());
        Compilation compilation = compilationRepository.save(CompilationMapper.toCompilation(compilationRequestDto, events));
        CompilationResponseDto compilationResponseDto = CompilationMapper.toCompilationResponseDto(compilation);
        viewsSetter.setViewsToEventsShortDto(compilationResponseDto.getEvents());
        log.info("Подборка с названием " + compilation.getTitle() + " была добавлена.");
        return compilationResponseDto;
    }

    @Override
    @Transactional
    public CompilationResponseDto updateCompilation(Long compilationId, CompilationRequestDto compilationRequestDto) {
        Compilation compilation = compilationRepository.findById(compilationId).orElseThrow(
                () -> new EntityNotFoundException("Подборка с id " + compilationId + " не найдена."));
        if (compilationRequestDto.getTitle() != null) {
            checkTitleExistence(compilationRequestDto.getTitle());
            compilation.setTitle(compilationRequestDto.getTitle());
        }
        if (compilationRequestDto.getEvents() != null && !compilationRequestDto.getEvents().isEmpty()) {
            List<Event> events = eventRepository.findAllByIdIn(compilationRequestDto.getEvents());
            compilation.setEvents(events);
        }
        if (compilationRequestDto.getPinned() != null) {
            compilation.setPinned(compilationRequestDto.getPinned());
        }
        CompilationResponseDto compilationResponseDto = CompilationMapper.toCompilationResponseDto(compilation);
        viewsSetter.setViewsToEventsShortDto(compilationResponseDto.getEvents());
        log.info("Подборка с названием " + compilation.getTitle() + " была изменена.");
        return compilationResponseDto;
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compilationId) {
        compilationRepository.findById(compilationId).orElseThrow(
                () -> new EntityNotFoundException("Подборка с id " + compilationId + " не найдена."));
        compilationRepository.deleteById(compilationId);
        log.info("Подборка с id " + compilationId + " была удалена.");
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationResponseDto getCompilationById(Long compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId).orElseThrow(
                () -> new EntityNotFoundException("Подборка с id " + compilationId + " не найдена."));
        CompilationResponseDto compilationResponseDto = CompilationMapper.toCompilationResponseDto(compilation);
        viewsSetter.setViewsToEventsShortDto(compilationResponseDto.getEvents());
        return compilationResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationResponseDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Compilation> compilationCriteriaQuery = criteriaBuilder.createQuery(Compilation.class);
        Root<Compilation> compilationRoot = compilationCriteriaQuery.from(Compilation.class);
        compilationCriteriaQuery.select(compilationRoot);

        if (pinned != null) {
            Predicate predicate = criteriaBuilder.equal(compilationRoot.get("pinned"), pinned);
            compilationCriteriaQuery.where(predicate);
        }

        TypedQuery<Compilation> typedQuery = entityManager.createQuery(compilationCriteriaQuery);
        PageRequest pageRequest = PageRequest.of((from / size), size);
        typedQuery.setFirstResult(pageRequest.getPageNumber());
        typedQuery.setMaxResults(pageRequest.getPageSize());
        List<Compilation> compilations = typedQuery.getResultList();

        List<CompilationResponseDto> compilationResponseDtos = compilations.stream()
                .map(CompilationMapper::toCompilationResponseDto)
                .collect(Collectors.toList());

        compilationResponseDtos.forEach(c -> viewsSetter.setViewsToEventsShortDto(c.getEvents()));
        return compilationResponseDtos;
    }

    private void checkTitleExistence(String title) {
        if (compilationRepository.existsByTitle(title)) {
            throw new ConflictOperationException("Подборка с названием " + title + " уже существует.");
        }
    }
}
