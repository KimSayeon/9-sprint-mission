package com.sprint.mission.discodeit.service.file;


import com.sprint.mission.discodeit.DTO.UserCreateRequest;
import com.sprint.mission.discodeit.DTO.UserResponse;
import com.sprint.mission.discodeit.DTO.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class FileUserService implements UserService {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileUserService() {
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
        User user = new User(
                request.displayName(),
                request.email(),
                request.password(),
                request.phoneNumber(),
                request.profileImageUrl()
        );
        saveUserFile(user);
        return UserResponse.from(user); //UserResponse로 변환해서 변환

    }

    @Override
    public UserResponse findById(UUID id) {
        return readUserFile(resolvePath(id))
                .map(UserResponse::from)
                .orElseThrow(() -> new NoSuchElementException("해당 ID를 가진 유저를 찾을 수 없습니다: " + id));
    }

    @Override
    public List<UserResponse> findAll() {
        try (Stream<Path> paths = Files.list(DIRECTORY)){
            return paths
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(this::readUserFile)
                    .flatMap(Optional::stream)
                    .map(UserResponse::from)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("전체 유저 목록을 읽어오는 중에 문제가 생겼습니다.", e);
        }
    }

    @Override
    public UserResponse update(UUID id, UserUpdateRequest request) {
        User user = readUserFile(resolvePath(id))
                .orElseThrow(() -> new RuntimeException("수정하려는 유저가 존재하지 않습니다. ID:" + id));

        user.update(
                request.displayName(),
                request.email(),
                request.password(),
                request.phoneNumber(),
                request.profileImageUrl()
        );
        saveUserFile(user);
        return UserResponse.from(user);
    }

    @Override
    public void deleteById(UUID id) {
        Path path = resolvePath(id);
        try{
            if (!Files.exists(path)) {
                throw new RuntimeException("삭제하려는 유저가 존재하지 않습니다. ID: " + id);
            }
            Files.delete(path);
        } catch (IOException e){
            throw new RuntimeException("유저 정보를 삭제하는 중 오류가 발생했습니다. ID: " +id);
        }
    }

    public Optional<UserResponse> findByDisplayName(String displayName){
        try (Stream<Path> paths = Files.list(DIRECTORY)){
            return paths
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(this::readUserFile)
                    .flatMap(Optional::stream)
                    .filter(user -> displayName.equals(user.getDisplayName()))
                    .map(UserResponse::from)
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
                    .map(UserResponse::from)
                    .findFirst();
        } catch (IOException e) {
            return Optional.empty();
        }
    }

}
