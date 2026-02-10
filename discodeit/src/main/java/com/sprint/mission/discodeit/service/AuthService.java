package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.LoginRequest;
import com.sprint.mission.discodeit.DTO.UserResponse;

public interface AuthService {
    // 로그인에 성공하면 유저 정보를 돌려줍니다.
    UserResponse login(LoginRequest request);
}