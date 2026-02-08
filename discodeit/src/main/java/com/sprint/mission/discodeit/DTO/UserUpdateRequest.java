package com.sprint.mission.discodeit.DTO;

import java.util.UUID;

public record UserUpdateRequest (
        String displayName,
        String email,
        String password,
        String phoneNumber,
        BinaryContentCreateRequest profileImage

){}
