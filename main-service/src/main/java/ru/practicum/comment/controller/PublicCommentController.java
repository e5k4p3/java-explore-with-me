package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PublicCommentController {
    private final CommentService commentService;

    @GetMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentResponseDto> getAllCommentsByEventId(@PathVariable Long eventId,
                                                            @RequestParam(defaultValue = "0") Integer from,
                                                            @RequestParam(defaultValue = "10") Integer size) {
        log.debug("Получен GET запрос на получение комментариев события.");
        return commentService.getAllCommentsByEventId(eventId, from, size);
    }

    @GetMapping("/users/{userId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentResponseDto> getAllCommentsByCommentatorId(@PathVariable Long userId,
                                                                  @RequestParam(defaultValue = "0") Integer from,
                                                                  @RequestParam(defaultValue = "10") Integer size) {
        log.debug("Получен GET запрос на получение комментариев пользователя.");
        return commentService.getAllCommentsByCommentatorId(userId, from, size);
    }

    @GetMapping("/users/{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentResponseDto> getAllCommentsByCommentatorIdAndEventId(@PathVariable Long userId,
                                                                            @PathVariable Long eventId,
                                                                            @RequestParam(defaultValue = "0") Integer from,
                                                                            @RequestParam(defaultValue = "10") Integer size) {
        log.debug("Получен GET запрос на получение комментариев пользователя, которые он написал на событие.");
        return commentService.getAllCommentsByCommentatorIdAndEventId(userId, eventId, from, size);
    }

    @GetMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponseDto getCommentById(@PathVariable Long commentId) {
        log.debug("Получен GET запрос на получение комментария по id.");
        return commentService.getCommentById(commentId);
    }
}
