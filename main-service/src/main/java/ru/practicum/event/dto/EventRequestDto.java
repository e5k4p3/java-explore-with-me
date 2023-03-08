package ru.practicum.event.dto;

import lombok.*;
import ru.practicum.event.model.enums.EventStateAction;
import ru.practicum.util.Create;
import ru.practicum.util.Update;

import javax.validation.constraints.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EventRequestDto {
    @NotNull(message = "Примечание события не может быть null.", groups = Create.class)
    @NotBlank(message = "Примечание события не может быть пустым.", groups = Create.class)
    @Size(min = 20, max = 2000, groups = {Create.class, Update.class})
    private String annotation;
    @NotNull(message = "Id категории не может быть null.", groups = Create.class)
    @Positive(message = "Id категории не может быть отрицательным.", groups = {Create.class, Update.class})
    private Long category;
    @NotNull(message = "Описание события не может быть null.", groups = Create.class)
    @NotBlank(message = "Описание события не может быть пустым.", groups = Create.class)
    @Size(min = 20, max = 7000, groups = {Create.class, Update.class})
    private String description;
    @NotNull(message = "Время начала события не может быть null.", groups = Create.class)
    @NotBlank(message = "Время начала события не может быть пустым.", groups = Create.class)
    private String eventDate;
    @NotNull(message = "Координаты события не могут быть null.", groups = Create.class)
    private LocationDto location;
    @NotNull(message = "Статус оплаты события не может быть null.", groups = Create.class)
    private Boolean paid;
    @NotNull(message = "Ограничение кол-ва участников события не может быть null.", groups = Create.class)
    @PositiveOrZero(message = "Ограничение кол-ва участников события должно быть положительным или 0.", groups = {Create.class, Update.class})
    private Integer participantLimit;
    @NotNull(message = "Запрос на модерацию события не может быть null.", groups = Create.class)
    private Boolean requestModeration;
    @NotNull(message = "Название события не может быть null.", groups = Create.class)
    @NotBlank(message = "Название события не может быть пустым.", groups = Create.class)
    @Size(min = 3, max = 120, groups = {Create.class, Update.class})
    private String title;
    private EventStateAction stateAction;
}
