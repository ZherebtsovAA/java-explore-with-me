package ru.practicum.main.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.category.dto.NewCategoryDto;
import ru.practicum.main.category.mapper.CategoryMapper;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.repository.CategoryRepository;
import ru.practicum.main.exception.ConflictException;
import ru.practicum.main.exception.NotFoundException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

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
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));

        //доработать
        //Существуют события, связанные с категорией

        categoryRepository.deleteById(catId);
    }

    @Transactional
    @Override
    public CategoryDto patchUpdate(Long catId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));

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

    public List<CategoryDto> findAll(PageRequest pageRequest) {
        return categoryMapper.toCategoryDto(categoryRepository.findAll(pageRequest));
    }

    public CategoryDto findById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));

        return categoryMapper.toCategoryDto(category);
    }
}