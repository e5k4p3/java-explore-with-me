package ru.practicum.exceptions;

public class ConflictOperationException extends RuntimeException {
    public ConflictOperationException(final String message) {
        super(message);
    }
}
