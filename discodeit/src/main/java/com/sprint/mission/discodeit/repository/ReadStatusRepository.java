package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {
    ReadStatus save(ReadStatus readStatus);
    Optional<ReadStatus> findById(UUID id);
    List<ReadStatus> findAll();
    void deleteById(UUID id);
    void deleteAllByChannelId(UUID channelId); //이 채널에 속한 모든 것 지우기
    void deleteByUserId(UUID userId); //유저 엔티티 안에 들어있는 사진id를 꺼내서 지우기
}
