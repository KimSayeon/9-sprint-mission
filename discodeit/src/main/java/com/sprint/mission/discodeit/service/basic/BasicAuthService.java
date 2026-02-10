package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.LoginRequest;
import com.sprint.mission.discodeit.DTO.UserResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserResponse login(LoginRequest request) {
        // 1. 이메일로 유저 찾기 (없으면 예외 발생)
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new NoSuchElementException("아이디 또는 비밀번호가 일치하지 않습니다."));

        // 2. 비밀번호 확인 (틀리면 예외 발생)
        if (!user.getPassword().equals(request.password())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        // 3. (선택사항) 로그인 했으니 '마지막 접속 시간' 업데이트 해주면 좋겠죠?
        userStatusRepository.findByUserId(user.getId())
                .ifPresent(UserStatus::updateLastLogin);

        // 4. 유저 응답 객체 만들어서 반환
        return convertToResponse(user);
    }

    private UserResponse convertToResponse(User user) {
        boolean isOnline = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(false);
        return UserResponse.from(user, isOnline);
    }
}