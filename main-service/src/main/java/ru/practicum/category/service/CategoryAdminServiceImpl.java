package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryAdminServiceImpl implements CategoryAdminService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CategoryDto save(NewCategoryDto newCategoryDto) {
        if (categoryRepository.exists(Example.of(categoryMapper.toCategory(newCategoryDto)))) {
            throw new ConflictException("Нарушение целостности данных. Категория с name=" + newCategoryDto.getName() + " создана ранее");
        }
        Category category = categoryRepository.save(categoryMapper.toCategory(newCategoryDto));

        return categoryMapper.toCategoryDto(category);
    }

    @Transactional
    @Override
    public void deleteById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id=" + catId + " не найдена"));

        Event templateSearchEvent = new Event();
        templateSearchEvent.setCategory(category);
        if (eventRepository.exists(Example.of(templateSearchEvent))) {
            throw new ConflictException("Существуют события, связанные с удаляемой категорией");
        }

        categoryRepository.deleteById(catId);
    }

    @Transactional
    @Override
    public CategoryDto patchUpdate(Long catId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id=" + catId + " не найдена"));

        if (categoryDto.getName().equals(category.getName())) {
            return categoryMapper.toCategoryDto(category);
        }

        Category templateSearchCategory = new Category();
        templateSearchCategory.setName(categoryDto.getName());
        if (categoryRepository.exists(Example.of(templateSearchCategory))) {
            throw new ConflictException("Нарушение целостности данных. Категория с name=" + categoryDto.getName() + " уже существует");
        }

        category.setName(categoryDto.getName());
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }
}