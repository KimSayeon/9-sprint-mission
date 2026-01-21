package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private String displayName;
    private String email;
    private String phoneNumber;
    private Long createdAt;
    private Long updatedAt;

    public User(String displayName, String email, String phoneNumber) {
        this.id = UUID.randomUUID();
        this.displayName = displayName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void update(String displayName, String email, String phoneNumber){
        if (displayName != null) this.displayName = displayName;
        if (email != null) this.email = email;
        if (phoneNumber != null) this.phoneNumber = phoneNumber;
        this.updatedAt = System.currentTimeMillis();
    }


    public UUID getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + this.id +
                ", displayName='" + this.displayName + '\'' +
                ", email='" + this.email + '\'' +
                ", phoneNumber='" + this.phoneNumber + '\'' +
                ", createdAt=" + this.createdAt +
                ", updatedAt=" + this.updatedAt +
                '}'; //출력해보기 예쁘게!
    }
}
