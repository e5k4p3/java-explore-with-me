package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;
import ru.practicum.util.ValidationErrorsHandler;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@Valid @RequestBody CategoryDto categoryDto, BindingResult bindingResult) {
        log.debug("[ADMIN] Получен POST запрос на добавление категории.");
        ValidationErrorsHandler.logValidationErrors(bindingResult);
        return categoryService.addCategory(categoryDto);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long categoryId) {
        log.debug("[ADMIN] Получен DELETE запрос на удаление категории.");
        categoryService.deleteCategory(categoryId);
    }

    @PatchMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(@Valid @RequestBody CategoryDto categoryDto,
                                      BindingResult bindingResult,
                                      @PathVariable Long categoryId) {
        log.debug("[ADMIN] Получен PATCH запрос на изменение категории.");
        ValidationErrorsHandler.logValidationErrors(bindingResult);
        return categoryService.updateCategory(categoryDto, categoryId);
    }
}
