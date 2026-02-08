package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.UserCreateRequest;
import com.sprint.mission.discodeit.DTO.UserResponse;
import com.sprint.mission.discodeit.DTO.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class BasicUserService implements UserService {

    @Qualifier("fileUserRepository")
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    @Qualifier("fileUserStatusRepository")
    private final UserStatusRepository userStatusRepository;
    private final ReadStatusRepository readStatusRepository;
    private final BinaryContentService binaryContentService;


    @Override
    public UserResponse create(UserCreateRequest request){
        //중복체크 시작
        if (userRepository.findByDisplayName(request.displayName()).isPresent()){
            throw new IllegalArgumentException("이미 사용 중인 이름입니다: " + request.displayName());
        }

        if (userRepository.findByEmail(request.email()).isPresent()){
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + request.email());
        }
        //bBinaryContent 처리
        UUID profileImageId = null; //일단 빈자리 만들어 둠
        if (request.profileImage() != null){
           profileImageId = binaryContentService.create(request.profileImage()).id();
        }

        User user = new User(
                request.displayName(),
                request.email(),
                request.password(),
                request.phoneNumber(),
                profileImageId
        );
        User savedUser = userRepository.save(user);

        if (savedUser == null) {
            throw new RuntimeException("유저 생성에 실패했습니다. 입력 정보를 다시 확인해주세요.");
        }

        UserStatus userStatus = new UserStatus(savedUser.getId()); //UserStatus 동시생성
        userStatusRepository.save(userStatus);

        return convertToResponse(savedUser, userStatus.isOnline());
    }

    @Override
    public UserResponse findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("조회 실패: ID(" + id + ") 유저를 찾을 수 없습니다."));
        //한명 조회할 때의 상태값을 찾아서 배달
        boolean isOnline = userStatusRepository.findByUserId(id)
                .map(UserStatus::isOnline)
                .orElse(false);

        return convertToResponse(user, isOnline);
    }

    @Override
    public List<UserResponse> findAll() {
        //모든 유저 파일 읽어오기
        List<User> users = userRepository.findAll();

        //모든 상태 정보 한번에 가져와서 Map으로 만들기
        Map<UUID, Boolean> statusMap = userStatusRepository.findAll().stream()
                .collect(Collectors.toMap(
                        UserStatus::getUserId,
                        UserStatus::isOnline,
                        (existing, replacement) -> existing //기존데이터와 새로운 데이터가 충돌하면 기존거 선택!
                ));

        //유저 리스트를 돌면서 메모리에 있는 Map에서 상태를 빼서 응답 가방에 넣음!
        return users.stream()
                .map(user -> {
                    boolean isOnline = statusMap.getOrDefault(user.getId(), false);
                    return UserResponse.from(user, isOnline);
                })
                .toList();
    }

    @Override
    public UserResponse update(UUID id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("수정 실패: ID(" + id + ") 유저를 찾을 수 없습니다."));
        //사진 수정 - 새로운 사진이 들어오면 새로 저장하고 번호표 갱신
        UUID newProfileImageId = user.getProfileImageId();

        if (request.profileImage() != null) {
            if (user.getProfileImageId() != null){
                binaryContentService.deleteById(user.getProfileImageId());
            }
            newProfileImageId = binaryContentService.create(request.profileImage()).id();
        }
        //정보 갱신 - 새 번호표를 포함해 유저 정보 업데이트 , 위에서 만든 변수 넣기 (profileImage -> newProfileImageId)
        user.update(
                request.displayName(),
                request.email(),
                request.password(),
                request.phoneNumber(),
                newProfileImageId
        );

        User updatedUser = userRepository.save(user);

        boolean isOnline = userStatusRepository.findByUserId(id)
                .map(UserStatus::isOnline)
                .orElse(false);

        return convertToResponse(updatedUser, isOnline);
    }

    @Override
    public void deleteById(UUID id) {
        User user = userRepository.findById(id) //지울 유저 있는지 확인
                .orElseThrow(() -> new IllegalArgumentException("삭제 실패: ID(" + id + ") 유저를 찾을 수 없습니다."));

        UUID profileImageId = user.getProfileImageId();

        userStatusRepository.deleteByUserId(id);
        readStatusRepository.deleteByUserId(id);

        if(profileImageId != null) {
            binaryContentService.deleteById(profileImageId);
        }

        userRepository.deleteById(id);
    }

    //밖에서 받은 isOnline을 DTO에 담는 메소드
    private UserResponse convertToResponse(User user, boolean isOline) {
        return UserResponse.from(user, isOline);
    }

}
