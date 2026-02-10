package com.sprint.mission.discodeit.repository.file;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
@Primary
public class FileUserRepository implements UserRepository {
    private Path DIRECTORY;
    private final String EXTENSION = ".ser";

    @PostConstruct
    public void init() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", User.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException("저장 디렉토리를 생성할 수 없습니다.", e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id.toString() + EXTENSION);
    }

    private Optional<User> readUserFile(Path path) {
        if (!Files.exists(path)) return Optional.empty();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return Optional.ofNullable((User) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    private void saveUserFile(User user) {
        Path path = resolvePath(user.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException("유저 정보를 저장하는 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public User save(User user) {
        saveUserFile(user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return readUserFile(resolvePath(id));
    }

    @Override
    public List<User> findAll() {
        try (Stream<Path> paths = Files.list(DIRECTORY)) {
            return paths
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(this::readUserFile)
                    .flatMap(Optional::stream)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("전체 유저 목록을 읽어오는 중에 문제가 생겼습니다.", e);
        }
    }

    @Override
    public void deleteById(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("유저 정보를 삭제하는 중 오류가 발생했습니다. ID: " + id);
        }
    }

    @Override
    public Optional<User> findByDisplayName(String displayName) {
        try (Stream<Path> paths = Files.list(DIRECTORY)) {
            return paths
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(this::readUserFile)
                    .flatMap(Optional::stream)
                    .filter(user -> displayName.equals(user.getDisplayName()))
                    .findFirst();
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (Stream<Path> paths = Files.list(DIRECTORY)) {
            return paths
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(this::readUserFile)
                    .flatMap(Optional::stream)
                    .filter(user -> email.equals(user.getEmail()))
                    .findFirst();
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}