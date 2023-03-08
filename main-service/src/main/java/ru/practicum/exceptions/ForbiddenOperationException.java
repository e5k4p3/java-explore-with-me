package ru.practicum.exceptions;

public class ForbiddenOperationException extends RuntimeException {
    public ForbiddenOperationException(final String message) {
        super(message);
    }
}
