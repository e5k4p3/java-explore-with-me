package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HitRequestDto {
    @NotNull(message = "App name cannot be null.")
    @NotBlank(message = "App name cannot be blank.")
    private String app;
    @NotNull(message = "URI cannot be null.")
    @NotBlank(message = "URI cannot be blank.")
    private String uri;
    @NotNull(message = "IP cannot be null.")
    @NotBlank(message = "IP cannot be blank.")
    private String ip;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "Timestamp cannot be null.")
    private LocalDateTime timestamp;
}
