package ru.practicum.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDto {
    private Long id;
    @NotNull(message = "Имя пользователя не может быть null.")
    @NotBlank(message = "Имя пользователя не может быть пустым.")
    private String name;
    @NotNull(message = "Email пользователя не может быть null.")
    @Email(message = "Email пользователя должен быть корректным.")
    private String email;
}
