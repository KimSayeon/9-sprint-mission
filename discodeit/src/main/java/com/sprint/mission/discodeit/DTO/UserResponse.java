package com.sprint.mission.discodeit.DTO;

import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;

public record UserResponse (
        UUID id,
        String displayName,
        String email,
        String phoneNumber,
        String profileImageUrl
){
    public static UserResponse from(User user){
        return new UserResponse(
                user.getId(),
                user.getDisplayName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getProfileImageUrl()
        );
    }
}