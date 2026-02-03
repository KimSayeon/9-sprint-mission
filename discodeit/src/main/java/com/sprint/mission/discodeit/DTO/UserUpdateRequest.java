package com.sprint.mission.discodeit.DTO;

public record UserUpdateRequest (
        String displayName,
        String email,
        String password,
        String phoneNumber,
        String profileImageUrl
){}
