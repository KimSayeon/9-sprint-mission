package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository("JCFUserStatusRepository")
public class JCFUserStatusRepository implements UserStatusRepository {

    private final Map<UUID, UserStatus> repository = new HashMap<>();
    //지금은 HashMap으로 사용하지만 나중에 작업이 동시에 겹치면 데이터가 깨질 수 있어서 ConcurrentHaspMap을 사용하면 좋다고 함

    @Override
    public UserStatus save(UserStatus userStatus) {
        repository.put(userStatus.getId(), userStatus);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(repository.get(id));
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(repository.values());
    }

    @Override
    public void deleteById(UUID id) {
        repository.remove(id);
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        //UserStatus안에 있는 userId와 일치하는것 찾음
        return repository.values().stream()
                .filter(status -> status.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public void deleteByUserId(UUID userId) {
        //유저 삭제 시 관련 상태 정보도 함께 지움, userId에 맞는 데이터만 리스트에서 제거
        repository.values().removeIf(status -> status.getUserId().equals(userId));
    }
}
