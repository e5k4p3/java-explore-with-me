package ru.practicum.event.util;

import lombok.experimental.UtilityClass;
import ru.practicum.event.dto.LocationDto;
import ru.practicum.event.model.Location;

@UtilityClass
public final class LocationMapper {
    public static LocationDto toLocationDto(Location location) {
        return new LocationDto(
                location.getLat(),
                location.getLon()
        );
    }

    public static Location toLocation(LocationDto locationDto) {
        return new Location(
                locationDto.getLat(),
                locationDto.getLon()
        );
    }
}
