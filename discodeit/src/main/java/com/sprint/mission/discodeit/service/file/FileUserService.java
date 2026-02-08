package com.sprint.mission.discodeit.service.file;


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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service

public class FileUserService implements UserService {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository; //사진 저장하려면 사진 저장소 필요
    private final ReadStatusRepository readStatusRepository;

    public FileUserService(
            @Qualifier("fileUserStatusRepository") UserStatusRepository userStatusRepository,
            @Qualifier("fileBinaryContentRepository") BinaryContentRepository binaryContentRepository,
            @Qualifier("fileReadStatusRepository") ReadStatusRepository readStatusRepository){

        this.userStatusRepository = userStatusRepository;
        this.binaryContentRepository = binaryContentRepository; //주입받기
        this.readStatusRepository = readStatusRepository;

        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", User.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException("저장 디렉토리를 생성할 수 없습니다." , e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id.toString() + EXTENSION);
    }

    //공통로직 . 파일에서 유저 객체를 읽어오게 도와주는 메소드
    private Optional<User> readUserFile(Path path){
        if (!Files.exists(path)) return Optional.empty();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))){
            return Optional.ofNullable((User) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    //공통로직 . 유저 객체를 파일로 저장하게 도와주는 메소드
    private void saveUserFile(User user) {
        Path path = resolvePath(user.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))){
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException("유저 정보를 저장하는 중 오류가 발생했습니다.", e);
        }
    }



    @Override
    public UserResponse create(UserCreateRequest request) {
        //중복체크
        if (findByDisplayName(request.displayName()).isPresent()){
            throw new IllegalArgumentException("이미 사용 중인 이름입니다: " + request.displayName());
        }
        if (findByEmail(request.email()).isPresent()){
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + request.email());
        }

        //사진 데이터가 있으면 먼저 저장하고 UUID 받기
        UUID profileImageId = null;
        if (request.profileImage() != null){
            BinaryContent content = new BinaryContent(
                    request.profileImage().content(),
                    request.profileImage().contentType(),
                    request.profileImage().fileName()
            );
            //사진 저장소에 저장하고 ID 받기
            profileImageId = binaryContentRepository.save(content).getId();
        }
        //User생성자에 실제 파일 대신 번호표(UUID) 전달
        User user = new User(
                request.displayName(),
                request.email(),
                request.password(),
                request.phoneNumber(),
                profileImageId //<-여기에 UUID가 들어감
        );
        saveUserFile(user);

        //요구사항- 유저 생성 시 UserStatus도 같이 생성해야 함
        UserStatus userStatus = new UserStatus(user.getId());
        userStatusRepository.save(userStatus);

        return convertToResponse(user);

    }

    @Override
    public UserResponse findById(UUID id) {
        User user = readUserFile(resolvePath(id))
                .orElseThrow(() -> new NoSuchElementException("해당 ID를 가진 유저를 찾을 수 없습니다: " + id));
        return convertToResponse(user);
    }

    @Override
    public List<UserResponse> findAll() {
        try (Stream<Path> paths = Files.list(DIRECTORY)){
            return paths
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(this::readUserFile)
                    .flatMap(Optional::stream)
                    .map(this::convertToResponse)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("전체 유저 목록을 읽어오는 중에 문제가 생겼습니다.", e);
        }
    }

    @Override
    public UserResponse update(UUID id, UserUpdateRequest request) {
        User user = readUserFile(resolvePath(id))
                .orElseThrow(() -> new NoSuchElementException("수정하려는 유저가 존재하지 않습니다. ID:" + id));
        //수정할 사진이 있으면 저장하고 새번호 받기
        UUID newProfileImageId = user.getProfileImageId();

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
        //User 업데이트에 UUID 전달
        user.update(
                request.displayName(),
                request.email(),
                request.password(),
                request.phoneNumber(),
                newProfileImageId //<-새 번호표 전달
        );

        saveUserFile(user); //무조건 바뀐 정보 다시 저장!!!!!!!!!!!!무조건!!!!!!!!!!
        return convertToResponse(user);
    }

    @Override
    public void deleteById(UUID id) {
        Path path = resolvePath(id);
        User user = readUserFile(path)
                .orElseThrow(() -> new NoSuchElementException("삭제하려는 유저가 존재하지 않습니다. ID: " + id));

        UUID profileImageId = user.getProfileImageId();
        userStatusRepository.deleteByUserId(id);
        readStatusRepository.deleteByUserId(id);

        if (profileImageId != null) {
            binaryContentRepository.deleteById(profileImageId);
        }

        try {
            Files.delete(path);
        } catch (IOException e){
            throw new RuntimeException("유저 정보를 삭제하는 중 오류가 발생했습니다. ID: " +id);
        }
    }

    private UserResponse convertToResponse(User user) {
        boolean isOnline = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(false);
        return UserResponse.from(user, isOnline);
    }

    public Optional<UserResponse> findByDisplayName(String displayName){
        try (Stream<Path> paths = Files.list(DIRECTORY)){
            return paths
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(this::readUserFile)
                    .flatMap(Optional::stream)
                    .filter(user -> displayName.equals(user.getDisplayName()))
                    .map(this::convertToResponse)
                    .findFirst();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<UserResponse> findByEmail(String email){
        try (Stream<Path> paths = Files.list(DIRECTORY)){
            return paths
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(this::readUserFile)
                    .flatMap(Optional::stream)
                    .filter(user -> email.equals(user.getEmail()))
                    .map(this::convertToResponse)
                    .findFirst();
        } catch (IOException e) {
            return Optional.empty();
        }
    }

}
