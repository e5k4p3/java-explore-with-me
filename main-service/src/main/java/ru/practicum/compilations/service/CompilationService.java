package ru.practicum.compilations.service;

import ru.practicum.compilations.dto.CompilationRequestDto;
import ru.practicum.compilations.dto.CompilationResponseDto;

import java.util.List;

public interface CompilationService {
    CompilationResponseDto addCompilation(CompilationRequestDto compilationRequestDto);

    CompilationResponseDto updateCompilation(Long compilationId, CompilationRequestDto compilationRequestDto);

    void deleteCompilation(Long compilationId);

    CompilationResponseDto getCompilationById(Long compilationId);

    List<CompilationResponseDto> getAllCompilations(Boolean pinned, Integer from, Integer size);
}
