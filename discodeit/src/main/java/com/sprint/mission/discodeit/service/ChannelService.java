package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.ChannelCreateRequest;
import com.sprint.mission.discodeit.DTO.ChannelResponse;
import com.sprint.mission.discodeit.DTO.ChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponse create(ChannelCreateRequest request); //입구 Request, 출구 Response
    ChannelResponse findById(UUID id);
    List<ChannelResponse> findAll();
    ChannelResponse update(UUID id, ChannelUpdateRequest request);
    void deleteById(UUID id);

}
