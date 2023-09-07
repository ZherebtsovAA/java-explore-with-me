package ru.practicum.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserDto {
    @NotBlank
    @Size(min = 6, max = 254)
    @Email
    private String email;
    private Long id;
    @NotBlank
    @Size(min = 2, max = 250)
    private String name;
}