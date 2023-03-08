package ru.practicum.compilations.dto;

import lombok.*;
import ru.practicum.event.dto.EventResponseShortDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CompilationResponseDto {
    private Long id;
    private String title;
    private List<EventResponseShortDto> events;
    private Boolean pinned;
}
