package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.DTO.*;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponse createPublicChannel(ChannelCreateRequest request);
    ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request);
    ChannelResponse findById(UUID id);
    List<ChannelResponse> findAll();
    ChannelResponse update(UUID id, ChannelUpdateRequest request);
    void deleteById(UUID id);

}
