package ru.practicum.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.model.Hit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface StatsHitsRepository extends JpaRepository<Hit, Long> {
    @Query("SELECT NEW ru.practicum.dto.StatsResponseDto(h.app, h.uri, COUNT(DISTINCT h.ip)) " +
            "FROM Hit h WHERE h.timestamp BETWEEN :start AND :end AND h.uri IN (:uris) " +
            "GROUP BY h.uri, h.ip, h.app " +
            "ORDER BY COUNT(DISTINCT h.ip) DESC")
    List<StatsResponseDto> findAllByIpUnique(LocalDateTime start, LocalDateTime end, Set<String> uris);

    @Query("SELECT NEW ru.practicum.dto.StatsResponseDto(h.app, h.uri, COUNT(h.ip)) " +
            "FROM Hit h WHERE h.timestamp BETWEEN :start AND :end AND h.uri IN (:uris) " +
            "GROUP BY h.uri, h.ip, h.app " +
            "ORDER BY COUNT(h.ip) DESC")
    List<StatsResponseDto> findAllByIp(LocalDateTime start, LocalDateTime end, Set<String> uris);
}
