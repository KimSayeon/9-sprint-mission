package com.sprint.mission.discodeit.DTO;

public record UserCreateRequest (
        String displayName,
        String email,
        String password,
        String phoneNumber,
        BinaryContentCreateRequest profileImage
){}