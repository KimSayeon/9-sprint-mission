package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString

public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id = UUID.randomUUID();
    private String name;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public Channel(String name) {
        this.name = name;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void update(String name){
        if (name != null) this.name = name;
        this.updatedAt = Instant.now();
    }
}



