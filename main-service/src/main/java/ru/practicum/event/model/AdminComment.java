package ru.practicum.event.model;

import lombok.*;

import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
public class AdminComment {
    private String comment;
    private LocalDateTime createdOn;
}