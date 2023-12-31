package ru.practicum.stats.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class ViewStats {
    private String app;
    private String uri;
    private int hits;
}