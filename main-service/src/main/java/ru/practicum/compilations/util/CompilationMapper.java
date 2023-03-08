package ru.practicum.compilations.util;

import lombok.experimental.UtilityClass;
import ru.practicum.compilations.dto.CompilationRequestDto;
import ru.practicum.compilations.dto.CompilationResponseDto;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.event.model.Event;
import ru.practicum.event.util.EventMapper;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public final class CompilationMapper {
    public static CompilationResponseDto toCompilationResponseDto(Compilation compilation) {
        return new CompilationResponseDto(
                compilation.getId(),
                compilation.getTitle(),
                compilation.getEvents().stream().map(EventMapper::toEventResponseShortDto).collect(Collectors.toList()),
                compilation.getPinned()
        );
    }

    public static Compilation toCompilation(CompilationRequestDto compilationRequestDto, List<Event> events) {
        return new Compilation(
                null,
                compilationRequestDto.getTitle(),
                events,
                compilationRequestDto.getPinned()
        );
    }
}
