package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.DTO.BinaryContentResponse;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {
    BinaryContentResponse create(BinaryContentCreateRequest request);
    BinaryContentResponse findById(UUID id);
    List<BinaryContentResponse> findAll(List<UUID> ids);
    void deleteById(UUID id);
}
