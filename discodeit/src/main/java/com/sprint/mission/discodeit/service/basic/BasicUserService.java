package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.UserCreateRequest;
import com.sprint.mission.discodeit.DTO.UserResponse;
import com.sprint.mission.discodeit.DTO.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public UserResponse create(UserCreateRequest request){
        //중복체크 시작
        if (userRepository.findByDisplayName(request.displayName()).isPresent()){
            throw new IllegalArgumentException("이미 사용 중인 이름입니다: " + request.displayName());
        }

        if (userRepository.findByEmail(request.email()).isPresent()){
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + request.email());
        }

        User user = new User(
                request.displayName(),
                request.email(),
                request.password(),
                request.phoneNumber(),
                request.profileImageUrl()
        );
        User savedUser = userRepository.save(user);

        if (savedUser == null) {
            throw new RuntimeException("유저 생성에 실패했습니다. 입력 정보를 다시 확인해주세요.");
        }

        UserStatus userStatus = new UserStatus(savedUser.getId());
        userStatusRepository.save(userStatus);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getDisplayName(),
                savedUser.getEmail(),
                savedUser.getPhoneNumber(),
                savedUser.getProfileImageUrl()
        );
    }

    @Override
    public UserResponse findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("조회 실패: ID(" + id + ") 유저를 찾을 수 없습니다."));

        return new UserResponse(
                user.getId(),
                user.getDisplayName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getProfileImageUrl()
        );
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getDisplayName(),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        user.getProfileImageUrl()
                ))
                .toList();
    }

    @Override
    public UserResponse update(UUID id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("수정 실패: ID(" + id + ") 유저를 찾을 수 없습니다."));

        user.update(
                request.displayName(),
                request.email(),
                request.password(),
                request.phoneNumber(),
                request.profileImageUrl()
        );

        User updatedUser = userRepository.save(user);

        return new UserResponse(
                updatedUser.getId(),
                updatedUser.getDisplayName(),
                updatedUser.getEmail(),
                updatedUser.getPhoneNumber(),
                updatedUser.getProfileImageUrl()
        );
    }

    @Override
    public void deleteById(UUID id) {
        User user = userRepository.findById(id) //지울 유저 있는지 확인
                .orElseThrow(() -> new IllegalArgumentException("삭제 실패: ID(" + id + ") 유저를 찾을 수 없습니다."));
        userStatusRepository.deleteById(id); //유저의 온/오프라인 정보 삭제
        binaryContentRepository.deleteById(id); //유저의 사진 정보 삭제
        readStatusRepository.deleteByUserId(id); //유저의 읽음 상태 전부 삭제
        userRepository.deleteById(id);  //유저 본체 삭제

    }

}
