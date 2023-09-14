package ru.practicum.category.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.category.dto.CategoryDto;

import java.util.List;

public interface CategoryPublicService {
    List<CategoryDto> findAll(PageRequest pageRequest);

    CategoryDto findById(Long catId);
}