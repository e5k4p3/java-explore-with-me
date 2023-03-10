package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.service.CommentService;
import ru.practicum.util.ValidationErrorsHandler;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto addComment(@PathVariable Long userId,
                                         @PathVariable Long eventId,
                                         @Valid @RequestBody CommentRequestDto commentRequestDto,
                                         BindingResult bindingResult) {
        log.debug("Получен POST на добавление комментария.");
        ValidationErrorsHandler.logValidationErrors(bindingResult);
        return commentService.addComment(userId, eventId, commentRequestDto);
    }

    @PatchMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponseDto updateComment(@PathVariable Long userId,
                                            @PathVariable Long commentId,
                                            @Valid @RequestBody CommentRequestDto commentRequestDto,
                                            BindingResult bindingResult) {
        log.debug("Получен PATCH запрос на изменение комментария.");
        ValidationErrorsHandler.logValidationErrors(bindingResult);
        return commentService.updateComment(userId, commentId, commentRequestDto);
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentById(@PathVariable Long userId,
                                  @PathVariable Long commentId) {
        log.debug("Получен DELETE запрос на удаление комментария по id.");
        commentService.deleteCommentById(userId, commentId);
    }
}
