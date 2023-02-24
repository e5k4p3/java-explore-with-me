package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitRequestDto;
import ru.practicum.dto.StatsResponseDto;
import ru.practicum.service.StatsHitsService;
import ru.practicum.utils.ValidationErrorsHandler;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsHitsController {
    private final StatsHitsService statsHitsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@Valid @RequestBody HitRequestDto requestDto,
                       BindingResult bindingResult) {
        ValidationErrorsHandler.logValidationErrors(bindingResult);
        log.debug("Получен POST запрос на добавление Hit-а.");
        statsHitsService.addHit(requestDto);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<StatsResponseDto> getStats(@RequestParam String start,
                                           @RequestParam String end,
                                           @RequestParam Set<String> uris,
                                           @RequestParam(defaultValue = "false") Boolean unique) {
        log.debug("Получен GET запрос на получение Stats");
        return statsHitsService.getHitsStats(start, end, uris, unique);
    }
}
