package ru.practicum.category.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.model.Category;

import java.util.List;

public interface CategoryPublicService {
    List<CategoryDto> findAll(PageRequest pageRequest);

    Category findById(Long catId);
}