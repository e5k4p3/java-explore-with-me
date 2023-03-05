package ru.practicum.compilations.dto;

import lombok.*;
import ru.practicum.util.Create;
import ru.practicum.util.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CompilationRequestDto {
    @NotNull(message = "Название подборки не может быть null.", groups = Create.class)
    @NotBlank(message = "Название подборки не может быть пустым.", groups = Create.class)
    @Size(max = 120, groups = {Create.class, Update.class})
    private String title;
    private List<Long> events;
    private Boolean pinned;
}
