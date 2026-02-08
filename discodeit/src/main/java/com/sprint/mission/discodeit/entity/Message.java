package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
    private List<UUID> attachmentIds = new ArrayList<>();

    public Message(String content, UUID userId, UUID channelId){
        this.content = content;
        this.userId = userId;
        this.channelId = channelId;
    }

    public void update(String content){
        if(content != null) this.content = content;
        this.updatedAt = Instant.now();
    }

    //첨부파일 ID를 목록에 넣는 메서드
    public void addAttachmentId(UUID attachmentId){
        this.attachmentIds.add(attachmentId);
    }

    //목록을 꺼내는 메서드
    public List<UUID> getAttachmentIds() {
        return attachmentIds;
    }
}
