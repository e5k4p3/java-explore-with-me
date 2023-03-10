package ru.practicum.comment.util;

import lombok.experimental.UtilityClass;
import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;
import ru.practicum.util.LocalDateTimeFormatter;

import java.time.LocalDateTime;

@UtilityClass
public final class CommentMapper {
    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getEvent().getId(),
                comment.getCommentator(),
                comment.getText(),
                LocalDateTimeFormatter.toString(comment.getCreated()),
                comment.getEdited()
        );
    }

    public static Comment toComment(CommentRequestDto commentRequestDto, Event event, User commentator) {
        return new Comment(
                commentRequestDto.getId(),
                event,
                commentator,
                commentRequestDto.getText(),
                LocalDateTime.now(),
                false
        );
    }
}
