package ru.practicum.event.model;

import lombok.*;

import javax.persistence.Embeddable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Embeddable
public class Location {
    private Double lat;
    private Double lon;
}
