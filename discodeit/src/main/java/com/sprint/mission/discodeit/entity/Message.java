package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@ToString

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id = UUID.randomUUID();  //고유아이디
    private UUID userId;
    private String content;  //작성 내용
    private UUID channelId;  //어느 채널에 썼는지
    private Instant createdAt = Instant.now();  //생성 시간
    private Instant updatedAt = Instant.now();   //수정 시간

    public Message(String content, UUID userId, UUID channelId){
        this.content = content;
        this.userId = userId;
        this.channelId = channelId;
    }

    public void update(String content){
        if(content != null) this.content = content;
        this.updatedAt = Instant.now();
    }
}
