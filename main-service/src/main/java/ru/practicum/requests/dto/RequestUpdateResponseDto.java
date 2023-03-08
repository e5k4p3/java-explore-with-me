package ru.practicum.requests.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RequestUpdateResponseDto {
    private List<RequestDto> confirmedRequests;
    private List<RequestDto> rejectedRequests;
}
