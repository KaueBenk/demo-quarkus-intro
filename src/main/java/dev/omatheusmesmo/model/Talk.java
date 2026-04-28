package dev.omatheusmesmo.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Talk extends PanacheEntity {

    @Column(nullable = false)
    public String title;

    @Column(nullable = false)
    public String speaker;

    @Column(nullable = false)
    public LocalDateTime startTime;

    @Column(nullable = false)
    public LocalDateTime endTime;

    @Column(nullable = false)
    public Level level;

    public Talk() {}

    public Talk(String title, String speaker, LocalDateTime startTime, LocalDateTime endTime, Level level) {
        this.title = title;
        this.speaker = speaker;
        this.startTime = startTime;
        this.endTime = endTime;
        this.level = level;
    }

    public void updateFrom(String title, String speaker, LocalDateTime startTime, LocalDateTime endTime, Level level) {
        this.title = title;
        this.speaker = speaker;
        this.startTime = startTime;
        this.endTime = endTime;
        this.level = level;
    }

    public static Talk findByTitle(String title) {
        return find("title", title).firstResult();
    }

    public static List<Talk> findByLevel(Level level) {
        return list("level", level);
    }
}
