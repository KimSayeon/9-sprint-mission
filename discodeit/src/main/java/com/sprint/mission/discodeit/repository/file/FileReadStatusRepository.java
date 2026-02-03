package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class FileReadStatusRepository implements ReadStatusRepository {
    private Map<UUID, ReadStatus> data = new HashMap<>();

    @Override
    public ReadStatus save(ReadStatus readStatus){
        data.put(readStatus.getId(), readStatus);
        saveToFile();
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id){
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID id){
        data.remove(id);
        saveToFile();
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        //channelId필드가 일치하는 기록 삭제
        data.values().removeIf(item -> item.getChannelId().equals(channelId));
        saveToFile();
    }

    @Override
    public void deleteByUserId(UUID userId){
        data.values().removeIf(item -> item.getUserId().equals(userId));
        saveToFile();
    }

    private void saveToFile(){

    }

}
