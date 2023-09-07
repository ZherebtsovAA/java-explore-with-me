package ru.practicum.event.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Location {
    private float lat;
    private float lon;
}