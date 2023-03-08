package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.dto.CommentResponseDto;

import java.util.List;

public interface CommentService {
    CommentResponseDto addComment(Long userId, Long eventId, CommentRequestDto commentRequestDto);

    CommentResponseDto updateComment(Long userId, Long commentId, CommentRequestDto commentRequestDto);

    void deleteCommentById(Long userId, Long commentId);

    void deleteCommentByIdByAdmin(Long commentId);

    CommentResponseDto getCommentById(Long commentId);

    List<CommentResponseDto> getAllCommentsByEventId(Long eventId, Integer from, Integer size);

    List<CommentResponseDto> getAllCommentsByCommentatorId(Long userId, Integer from, Integer size);

    List<CommentResponseDto> getAllCommentsByCommentatorIdAndEventId(Long userId, Long eventId, Integer from, Integer size);
}
