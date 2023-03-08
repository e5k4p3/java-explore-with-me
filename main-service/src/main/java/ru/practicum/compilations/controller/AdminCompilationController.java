package ru.practicum.compilations.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationRequestDto;
import ru.practicum.compilations.dto.CompilationResponseDto;
import ru.practicum.compilations.service.CompilationService;
import ru.practicum.util.Create;
import ru.practicum.util.Update;
import ru.practicum.util.ValidationErrorsHandler;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationResponseDto addCompilation(@Validated(Create.class) @RequestBody CompilationRequestDto compilationRequestDto,
                                                 BindingResult bindingResult) {
        log.debug("Получен POST запрос на добавление подборки.");
        ValidationErrorsHandler.logValidationErrors(bindingResult);
        return compilationService.addCompilation(compilationRequestDto);
    }

    @PatchMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationResponseDto updateCompilation(@PathVariable Long compilationId,
                                                    @Validated(Update.class) @RequestBody CompilationRequestDto compilationRequestDto,
                                                    BindingResult bindingResult) {
        log.debug("Получен PATCH запрос на изменение подборки.");
        ValidationErrorsHandler.logValidationErrors(bindingResult);
        return compilationService.updateCompilation(compilationId, compilationRequestDto);
    }

    @DeleteMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compilationId) {
        log.debug("Получен DELETE запрос на удаление подборки.");
        compilationService.deleteCompilation(compilationId);
    }
}
