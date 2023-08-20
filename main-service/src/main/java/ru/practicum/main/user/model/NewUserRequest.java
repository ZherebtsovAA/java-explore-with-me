package ru.practicum.main.user.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class NewUserRequest {
    @NotBlank
    @Size(min = 6, max = 254)
    @Email
    private String email;
    @NotBlank
    @Size(min = 2, max = 250)
    private String name;
}