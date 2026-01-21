package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(String displayName, String email, String phoneNumber);
    User findById(UUID id);
    List<User> findAll();
    User update(UUID id, String displayName, String email, String phoneNumber);
    void delete(UUID id);
}

