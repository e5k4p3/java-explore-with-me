package ru.practicum.event.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LocationDto {
    private Double lat;
    private Double lon;
}
