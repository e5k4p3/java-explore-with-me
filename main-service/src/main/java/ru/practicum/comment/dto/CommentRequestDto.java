package ru.practicum.comment.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentRequestDto {
    private Long id;
    @NotNull(message = "Текст комментария не может быть null.")
    @NotBlank(message = "Текст комментария не может быть пустым.")
    private String text;
}
