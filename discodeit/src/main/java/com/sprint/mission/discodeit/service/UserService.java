package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.UserCreateRequest;
import com.sprint.mission.discodeit.DTO.UserResponse;
import com.sprint.mission.discodeit.DTO.UserUpdateRequest;
import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse create(UserCreateRequest request);
    UserResponse findById(UUID id);
    List<UserResponse> findAll();
    UserResponse update(UUID id, UserUpdateRequest request);
    void deleteById(UUID id);
}

