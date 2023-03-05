package ru.practicum.compilations.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationResponseDto;
import ru.practicum.compilations.service.CompilationService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationResponseDto> getAllCompilations(@RequestParam(defaultValue = "false") Boolean pinned,
                                                           @RequestParam(defaultValue = "0") Integer from,
                                                           @RequestParam(defaultValue = "10") Integer size) {
        log.debug("Получен GET запрос на получение всех подборок.");
        return compilationService.getAllCompilations(pinned, from, size);
    }

    @GetMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationResponseDto getCompilationById(@PathVariable Long compilationId) {
        log.debug("Получен GET запрос на получение подборки по id.");
        return compilationService.getCompilationById(compilationId);
    }
}
