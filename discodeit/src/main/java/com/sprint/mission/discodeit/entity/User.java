package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;


@Getter
@ToString

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id = UUID.randomUUID();
    private String displayName;
    private String email;
    private String password;
    private String phoneNumber;
    private UUID profileImageId;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public  User(String displayName, String email, String password, String phoneNumber, UUID profileImageId){
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.profileImageId = profileImageId;
    }

    public void update(String displayName, String email, String password, String phoneNumber, UUID profileImageId){
        if (displayName != null) this.displayName = displayName;
        if (email != null) this.email = email;
        if (password != null) this.password = password;
        if (phoneNumber != null) this.phoneNumber = phoneNumber;
        if (profileImageId != null) this.profileImageId = profileImageId;
        this.updatedAt = Instant.now();
    }

}
