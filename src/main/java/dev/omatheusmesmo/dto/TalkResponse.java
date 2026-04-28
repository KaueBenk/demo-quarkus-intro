package dev.omatheusmesmo.dto;

import java.time.LocalDateTime;

import dev.omatheusmesmo.model.Level;
import dev.omatheusmesmo.model.Talk;

public record TalkResponse(
        Long id,
        String title,
        String speaker,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Level level
) {
    public static TalkResponse from(Talk talk) {
        return new TalkResponse(
                talk.id,
                talk.title,
                talk.speaker,
                talk.startTime,
                talk.endTime,
                talk.level
        );
    }
}
