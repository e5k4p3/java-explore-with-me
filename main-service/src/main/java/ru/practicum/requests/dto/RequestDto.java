package ru.practicum.requests.dto;

import lombok.*;
import ru.practicum.requests.model.enums.RequestStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RequestDto {
    private Long id;
    private Long event;
    private Long requester;
    private LocalDateTime created;
    private RequestStatus status;
}
