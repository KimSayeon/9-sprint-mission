package com.sprint.mission.discodeit.DTO;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest (
        List<UUID> userIds
){}