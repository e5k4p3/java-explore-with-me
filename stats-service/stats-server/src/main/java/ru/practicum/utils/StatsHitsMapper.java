package ru.practicum.utils;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.HitRequestDto;
import ru.practicum.model.Hit;

@UtilityClass
public final class StatsHitsMapper {
    public static Hit toHit(HitRequestDto requestDto) {
        return new Hit(
                null,
                requestDto.getApp(),
                requestDto.getUri(),
                requestDto.getIp(),
                requestDto.getTimestamp()
        );
    }
}
