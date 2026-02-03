package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id = UUID.randomUUID();
    private UUID userId;
    private UUID messageId;
    private byte[] content;
    private Instant createdAt = Instant.now();

    public BinaryContent(UUID userId, UUID messageId, byte[] content){
        this.userId = userId;
        this.messageId = messageId;
        this.content = content;
    }
}
