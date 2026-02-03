package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString

public class ReadStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id = UUID.randomUUID();
    private UUID userId;
    private UUID channelId;
    private Instant lastReadAt = Instant.now();
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public ReadStatus(UUID userId, UUID channelId){
        this.userId = userId;
        this.channelId = channelId;
    }

    public void updateLastRead(Instant lastReadAt){
        if(lastReadAt != null){
            this.lastReadAt = lastReadAt;
            this.updatedAt = Instant.now();
        }
    }
}
