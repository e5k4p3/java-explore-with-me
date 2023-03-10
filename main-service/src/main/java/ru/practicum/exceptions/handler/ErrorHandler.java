package ru.practicum.exceptions.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.category.controller.AdminCategoryController;
import ru.practicum.category.controller.PublicCategoryController;
import ru.practicum.comment.controller.AdminCommentController;
import ru.practicum.comment.controller.PrivateCommentController;
import ru.practicum.comment.controller.PublicCommentController;
import ru.practicum.compilations.controller.AdminCompilationController;
import ru.practicum.compilations.controller.PublicCompilationController;
import ru.practicum.event.controller.AdminEventController;
import ru.practicum.event.controller.PrivateEventController;
import ru.practicum.event.controller.PublicEventController;
import ru.practicum.exceptions.*;
import ru.practicum.requests.controller.PrivateRequestController;
import ru.practicum.user.controller.AdminUserController;
import ru.practicum.util.LocalDateTimeFormatter;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice(assignableTypes = {
        AdminCategoryController.class,
        PublicCategoryController.class,
        AdminCompilationController.class,
        PublicCompilationController.class,
        AdminEventController.class,
        PrivateEventController.class,
        PublicEventController.class,
        PrivateRequestController.class,
        AdminUserController.class,
        AdminCommentController.class,
        PrivateCommentController.class,
        PublicCommentController.class
})
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final ValidationException e) {
        log.error(e.getMessage());
        return new ErrorResponse(
                "BAD_REQUEST",
                "Ошибка валидации.",
                e.getMessage(),
                LocalDateTimeFormatter.toString(LocalDateTime.now())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExists(final EntityAlreadyExistsException e) {
        log.error(e.getMessage());
        return new ErrorResponse(
                "CONFLICT",
                "Объект уже существует.",
                e.getMessage(),
                LocalDateTimeFormatter.toString(LocalDateTime.now())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNumberFormat(final NumberFormatException e) {
        log.error(e.getMessage());
        return new ErrorResponse(
                "BAD_REQUEST",
                "Ошибка параметров запроса.",
                e.getMessage(),
                LocalDateTimeFormatter.toString(LocalDateTime.now())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final EntityNotFoundException e) {
        log.error(e.getMessage());
        return new ErrorResponse(
                "NOT_FOUND",
                "Объект не найден.",
                e.getMessage(),
                LocalDateTimeFormatter.toString(LocalDateTime.now())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbidden(final ForbiddenOperationException e) {
        log.error(e.getMessage());
        return new ErrorResponse(
                "FORBIDDEN",
                "Недопустимая операция.",
                e.getMessage(),
                LocalDateTimeFormatter.toString(LocalDateTime.now())
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(final ConflictOperationException e) {
        log.error(e.getMessage());
        return new ErrorResponse(
                "CONFLICT",
                "Конфликт между данными.",
                e.getMessage(),
                LocalDateTimeFormatter.toString(LocalDateTime.now())
        );
    }
}
