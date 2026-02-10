package com.sprint.mission.discodeit.DTO;

import java.util.List;
import java.util.UUID;

public record PrivateChannelCreateRequest(
        List<UUID> memberIds // [추가] 이제 서비스에서 이 변수를 찾을 수 있습니다!
) {}