package com.sprint.mission.discodeit.DTO;

public record LoginRequest(
        String email,    // 아이디 대신 이메일을 사용합니다.
        String password
) {}