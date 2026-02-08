package com.sprint.mission.discodeit.DTO;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequest (
        String content,
        UUID userId,
        UUID channelId,
        List<BinaryContentCreateRequest> attachments
){}
