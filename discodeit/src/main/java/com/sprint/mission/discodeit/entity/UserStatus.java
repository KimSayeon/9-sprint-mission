package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
@ToString
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id = UUID.randomUUID();
    private UUID userId;
    private Instant lastLogin = Instant.now();
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public UserStatus(UUID userId){
        this.userId = userId;
    }

    public boolean isOnline(){
        return lastLogin.isAfter(Instant.now().minus(5, ChronoUnit.MINUTES));
    }

    public void updateLastLogin(){
        this.lastLogin = Instant.now();
        this.updatedAt = Instant.now();
    }

}
