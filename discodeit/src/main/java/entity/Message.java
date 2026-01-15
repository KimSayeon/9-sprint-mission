package entity;

import java.util.UUID;

public class Message {
    private UUID id;  //고유아이디
    private UUID userId;  //작성자
    private String content;  //작성 내용
    private UUID channelId;  //어느 채널에 썼는지
    private Long createdAt;  //생성 시간
    private Long updatedAt;  //수정 시간

    public Message(String content, UUID userId, UUID channelId){
        this.id = UUID.randomUUID();
        this.content = content;
        this.userId = userId;
        this.channelId = channelId;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void update(String content){
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + this.id +
                ", userId=" + this.userId +
                ", content='" + this.content + '\'' +
                ", channelId=" + this.channelId +
                ", createdAt=" + this.createdAt +
                ", updatedAt=" + this.updatedAt +
                '}';
    }
}
