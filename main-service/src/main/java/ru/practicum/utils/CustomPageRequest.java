package ru.practicum.utils;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@UtilityClass
public class CustomPageRequest {

    public PageRequest getPageRequest(int from, int size) {
        int page = getPageNumber(from, size);
        return PageRequest.of(page, size);
    }

    public PageRequest getPageRequest(int from, int size, Sort sort) {
        int page = getPageNumber(from, size);
        return PageRequest.of(page, size, sort);
    }

    private int getPageNumber(int from, int size) {
        return from / size;
    }
}