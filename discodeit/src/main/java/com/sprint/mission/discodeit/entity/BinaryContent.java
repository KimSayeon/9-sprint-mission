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
//    private UUID userId;
//    private UUID messageId; 독립성 확보
    private byte[] content;
    private String contentType;
    private String fileName;
    private Instant createdAt = Instant.now();

    public BinaryContent(byte[] content, String contentType, String fileName){
        this.content = content;
        this.contentType = contentType;
        this.fileName = fileName;
    }
}
