package entity;

import java.util.UUID;

public class Channel {
    private final UUID id;
    private String name;
    private Long createdAt;
    private Long updatedAt;

    public Channel(String name) {
        this.id = UUID.randomUUID();
        this.name = name;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void update(String name){
        if (name != null) this.name = name;
        this.updatedAt = System.currentTimeMillis();
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public String getName() {
        return name;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + this.id +
                ", name='" + this.name + '\'' +
                ", createdAt=" + this.createdAt +
                ", updatedAt=" + this.updatedAt +
                '}';
    }
}



