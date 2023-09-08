package ru.practicum.compilations.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class NewCompilationDto {
    private boolean pinned; //default: false
    @NotBlank
    @Size(min = 1, max = 50)
    private String title;
    private Set<Long> events;
}