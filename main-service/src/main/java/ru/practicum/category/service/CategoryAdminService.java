package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

public interface CategoryAdminService {
    CategoryDto save(NewCategoryDto newCategoryDto);

    void deleteById(Long catId);

    CategoryDto patchUpdate(Long catId, CategoryDto categoryDto);
}