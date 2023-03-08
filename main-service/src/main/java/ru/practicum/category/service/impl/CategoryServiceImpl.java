package ru.practicum.category.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dao.CategoryRepository;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.service.CategoryService;
import ru.practicum.category.util.CategoryMapper;
import ru.practicum.event.dao.EventRepository;
import ru.practicum.exceptions.ConflictOperationException;
import ru.practicum.exceptions.EntityAlreadyExistsException;
import ru.practicum.exceptions.EntityNotFoundException;
import ru.practicum.util.PageableMaker;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CategoryDto addCategory(CategoryDto categoryDto) {
        checkNameExistence(categoryDto.getName());
        Category category = categoryRepository.save(CategoryMapper.toCategory(categoryDto));
        log.info("Категория с названием " + category.getName() + " была добавлена.");
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId) {
        checkIdExistence(categoryId);
        checkNameExistence(categoryDto.getName());
        Category categoryToUpdate = categoryRepository.findById(categoryId).get();
        categoryToUpdate.setName(categoryDto.getName());
        log.info("Название категории с id " + categoryToUpdate.getId() + " было изменено на " + categoryToUpdate.getName() + ".");
        return CategoryMapper.toCategoryDto(categoryToUpdate);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        checkIdExistence(categoryId);
        if (eventRepository.existsByCategoryId(categoryId)) {
            throw new ConflictOperationException("Нельзя удалить категорию, которая используется в событии.");
        }
        categoryRepository.deleteById(categoryId);
        log.info("Категория с id " + categoryId + " была удалена.");
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long categoryId) {
        checkIdExistence(categoryId);
        return CategoryMapper.toCategoryDto(categoryRepository.findById(categoryId).get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        return categoryRepository.findAll(PageableMaker.makePage(from, size)).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    private void checkIdExistence(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new EntityNotFoundException("Категория с id " + categoryId + " не найдена.");
        }
    }

    private void checkNameExistence(String categoryName) {
        if (categoryRepository.existsByName(categoryName)) {
            throw new EntityAlreadyExistsException("Категория с названием " + categoryName + " уже существует.");
        }
    }
}
