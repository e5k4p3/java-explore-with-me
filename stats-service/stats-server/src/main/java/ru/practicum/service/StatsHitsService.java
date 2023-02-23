package ru.practicum.service;

import ru.practicum.dto.HitRequestDto;
import ru.practicum.dto.StatsResponseDto;

import java.util.List;
import java.util.Set;

public interface StatsHitsService {
    void addHit(HitRequestDto requestDto);
    List<StatsResponseDto> getHitsStats(String start, String end, Set<String> uris, Boolean unique);
}
