package ru.practicum.user.dto;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Value
public class UserDto {
    @NotBlank
    @Size(min = 6, max = 254)
    @Email
    String email;
    Long id;
    @NotBlank
    @Size(min = 2, max = 250)
    String name;
}