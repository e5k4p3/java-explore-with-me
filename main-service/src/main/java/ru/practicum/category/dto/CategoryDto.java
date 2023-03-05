package ru.practicum.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CategoryDto {
    private Long id;
    @NotNull(message = "Название категории не может быть null.")
    @NotBlank(message = "Название категории не может быть пустым.")
    private String name;
}
