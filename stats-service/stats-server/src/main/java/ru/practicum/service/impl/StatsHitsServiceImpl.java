package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dao.StatsHitsRepository;
import ru.practicum.dto.HitRequestDto;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.exceptions.TimeParamsException;
import ru.practicum.model.Hit;
import ru.practicum.service.StatsHitsService;
import ru.practicum.utils.StatsHitsMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsHitsServiceImpl implements StatsHitsService {
    private final StatsHitsRepository repository;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void addHit(HitRequestDto requestDto) {
        Hit hit = StatsHitsMapper.toHit(requestDto);
        log.info("Hit с uri " + hit.getUri() + " и ip " + hit.getIp() + " был добавлен.");
        repository.save(hit);
    }

    public List<StatsResponseDto> getHitsStats(String start, String end, Set<String> uris, Boolean unique) {
        LocalDateTime startTime;
        LocalDateTime endTime;
        try {
            startTime = LocalDateTime.parse(start, DATE_TIME_FORMATTER);
            endTime = LocalDateTime.parse(end, DATE_TIME_FORMATTER);
        } catch (Exception e) {
            throw new TimeParamsException("Неправильный формат параметров даты.");
        }
        checkTime(startTime, endTime);
        if (unique) {
            return repository.findAllByIpUnique(startTime, endTime, uris);
        } else {
            return repository.findAllByIp(startTime, endTime, uris);
        }
    }

    private void checkTime(LocalDateTime start, LocalDateTime end) {
        if(start.isAfter(end)) {
            throw new TimeParamsException("Время окончание не может быть раньше старта.");
        }
    }
}