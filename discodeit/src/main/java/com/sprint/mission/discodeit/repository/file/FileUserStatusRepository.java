package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository //스프링한테 이 파일이 일꾼(bean)이라고 알려주는 필수 마크!!
@Primary
public class FileUserStatusRepository implements UserStatusRepository {
    private Map<UUID, UserStatus> data = new HashMap<>();
    // UUID를 열쇠로, UserStatus를 내용물로 저장하는 지도Map
    @Override
    public UserStatus save(UserStatus userStatus){
        data.put(userStatus.getId(), userStatus);
        saveToFile();
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id){
        return Optional.ofNullable(data.get(id)); //orElse ?로 멘트??
    }

    @Override
    public List<UserStatus> findAll(){
        return new ArrayList<>(data.values());
    }


    @Override
    public void deleteById(UUID targetId){ //부모(유저)의 Id를 받음
        //data서랍안에 있는 내용물들 중, getTargetId가 유저 Id와 같은것만 골라 지움
        data.values().removeIf((UserStatus item) -> item.getUserId().equals(targetId));
        saveToFile(); //지운 후 저장
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId){
        return data.values().stream()
                .filter(userStatus -> userStatus.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public void deleteByUserId(UUID userId){
        Optional<UserStatus> target = findByUserId(userId);

        target.ifPresent(userStatus -> {
            data.remove(userStatus.getId());
            saveToFile();
        });
    }
    private void saveToFile(){
        //나중에 구현
    }

}
