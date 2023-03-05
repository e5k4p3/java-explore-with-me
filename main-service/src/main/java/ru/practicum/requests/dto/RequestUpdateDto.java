package ru.practicum.requests.dto;

import lombok.*;
import ru.practicum.requests.model.enums.RequestStatus;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RequestUpdateDto {
    @NotNull(message = "Список id заявок не может быть null.")
    @NotEmpty(message = "Список id заявок не может быть пустым.")
    private Set<Long> requestIds;
    @NotNull(message = "Статус подтверждения/отклонения заявок не может быть null.")
    private RequestStatus status;
}
