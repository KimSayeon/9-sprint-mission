package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.UserCreateRequest;
import com.sprint.mission.discodeit.DTO.UserResponse;
import com.sprint.mission.discodeit.DTO.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    // 이제 FileRepository를 직접 쓰는 게 아니라 인터페이스(UserRepository)를 씁니다!
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public UserResponse create(UserCreateRequest request) {
        // 1. 중복 체크
        if (userRepository.findByDisplayName(request.displayName()).isPresent()){
            throw new IllegalArgumentException("이미 사용 중인 이름입니다: " + request.displayName());
        }
        if (userRepository.findByEmail(request.email()).isPresent()){
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + request.email());
        }

        // 2. 프로필 이미지 저장 (있으면)
        UUID profileImageId = null;
        if (request.profileImage() != null){
            BinaryContent content = new BinaryContent(
                    request.profileImage().content(),
                    request.profileImage().contentType(),
                    request.profileImage().fileName()
            );
            profileImageId = binaryContentRepository.save(content).getId();
        }

        // 3. 유저 객체 생성
        User user = new User(
                request.displayName(),
                request.email(),
                request.password(),
                request.phoneNumber(),
                profileImageId
        );

        // 4. 저장 (이제 파일 저장은 Repository가 알아서 함)
        userRepository.save(user);

        // 5. UserStatus 생성
        UserStatus userStatus = new UserStatus(user.getId());
        userStatusRepository.save(userStatus);

        return convertToResponse(user);
    }

    @Override
    public UserResponse findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 ID를 가진 유저를 찾을 수 없습니다: " + id));
        return convertToResponse(user);
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public UserResponse update(UUID id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("수정하려는 유저가 존재하지 않습니다. ID:" + id));

        UUID newProfileImageId = user.getProfileImageId();

        // 이미지 수정 로직
        if (request.profileImage() != null){
            if (user.getProfileImageId() != null) {
                binaryContentRepository.deleteById(user.getProfileImageId());
            }
            BinaryContent newContent = new BinaryContent(
                    request.profileImage().content(),
                    request.profileImage().contentType(),
                    request.profileImage().fileName()
            );
            newProfileImageId = binaryContentRepository.save(newContent).getId();
        }

        // 정보 업데이트
        user.update(
                request.displayName(),
                request.email(),
                request.password(),
                request.phoneNumber(),
                newProfileImageId
        );

        userRepository.save(user); // 변경된 내용 저장
        return convertToResponse(user);
    }

    @Override
    public void deleteById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("삭제하려는 유저가 존재하지 않습니다. ID: " + id));

        UUID profileImageId = user.getProfileImageId();

        // 연관 데이터 삭제
        userStatusRepository.deleteByUserId(id);
        readStatusRepository.deleteByUserId(id);

        if (profileImageId != null) {
            binaryContentRepository.deleteById(profileImageId);
        }

        // 유저 삭제
        userRepository.deleteById(id);
    }

    private UserResponse convertToResponse(User user) {
        boolean isOnline = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(false);
        return UserResponse.from(user, isOnline);
    }
}