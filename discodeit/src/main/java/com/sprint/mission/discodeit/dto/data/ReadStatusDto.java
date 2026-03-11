package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusDto(
    UUID is,
    UUID userId,
    UUID channelId,
    Instant lastReadAt,
    Instant createdAt,
    Instant updatedAt
) {

}
