package ru.practicum.main.category.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto save(NewCategoryDto newCategoryDto);

    void deleteById(Long catId);

    CategoryDto patchUpdate(Long catId, CategoryDto categoryDto);

    List<CategoryDto> findAll(PageRequest pageRequest);

    CategoryDto findById(Long catId);
}