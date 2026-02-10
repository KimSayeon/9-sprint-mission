package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
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
public class FileMessageRepository implements MessageRepository {

    private Path DIRECTORY;
    private final String EXTENSION = ".ser";

    @PostConstruct
    public void init() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", Message.class.getSimpleName());
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

    private Optional<Message> readMessageFile(Path path) {
        if (!Files.exists(path)) return Optional.empty();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return Optional.ofNullable((Message) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    private void saveMessageFile(Message message) {
        Path path = resolvePath(message.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException("메세지 정보를 저장하는 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public Message save(Message message) {
        saveMessageFile(message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return readMessageFile(resolvePath(id));
    }

    @Override
    public List<Message> findAll() {
        try (Stream<Path> paths = Files.list(DIRECTORY)) {
            return paths
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(this::readMessageFile)
                    .flatMap(Optional::stream)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("전체 메세지 목록을 읽어오는 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Files.deleteIfExists(resolvePath(id));
        } catch (IOException e) {
            throw new RuntimeException("메세지 삭제 실패: " + id, e);
        }
    }

    // [누락된 기능 추가 1] 채널 삭제 시 해당 채널 메시지 다 삭제
    @Override
    public void deleteAllByChannelId(UUID channelId) {
        findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .forEach(message -> deleteById(message.getId()));
    }

    // [누락된 기능 추가 2] 유저 삭제 시 해당 유저가 쓴 메시지 다 삭제 (이게 없어서 에러 났음!)
    @Override
    public void deleteByUserId(UUID userId) {
        findAll().stream()
                .filter(message -> message.getUserId().equals(userId))
                .forEach(message -> deleteById(message.getId()));
    }
}