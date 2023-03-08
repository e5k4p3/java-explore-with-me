package ru.practicum.exceptions;

public class EntityAlreadyExistsException extends RuntimeException {
    public EntityAlreadyExistsException(final String message) {
        super(message);
    }
}
