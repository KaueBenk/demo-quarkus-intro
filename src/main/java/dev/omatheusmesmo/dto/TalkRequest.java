package dev.omatheusmesmo.dto;

import java.time.LocalDateTime;
import dev.omatheusmesmo.model.Level;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TalkRequest(
    @NotBlank(message = "Title is required")
    String title,
    @NotBlank(message = "Speaker is required")
    String speaker,

    @NotNull(message = "Start time is required")
    LocalDateTime startTime,

    @NotNull(message = "End time is required")
    LocalDateTime endTime,

    @NotNull(message = "Level is required")
    Level level
) {}
