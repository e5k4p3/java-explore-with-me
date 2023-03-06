package ru.practicum.event.dto;

import lombok.*;

import javax.persistence.Embeddable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LocationDto {
    private Double lat;
    private Double lon;
}
