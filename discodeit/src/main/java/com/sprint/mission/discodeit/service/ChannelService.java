package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.*;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponse createPublicChannel(ChannelCreateRequest request);
    ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request);
    ChannelResponse findById(UUID id);

    // [중요] findAll()을 지우고 findAllByUserId()로 변경해야 오류가 사라집니다.
    List<ChannelResponse> findAllByUserId(UUID userId);

    ChannelResponse update(UUID id, ChannelUpdateRequest request);
    void deleteById(UUID id);
}