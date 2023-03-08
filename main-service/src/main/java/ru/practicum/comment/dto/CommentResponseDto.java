package ru.practicum.comment.dto;

import lombok.*;
import ru.practicum.user.model.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentResponseDto {
    private Long id;
    private Long eventId;
    private User commentator;
    private String text;
    private String created;
    private Boolean edited;
}
