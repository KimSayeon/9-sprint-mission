/*
package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.DTO.*;
import com.sprint.mission.discodeit.DTO.MessageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
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

public class FileChannelService implements ChannelService {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    public FileChannelService(
            @Qualifier("fileMessageRepository") MessageRepository messageRepository,
            @Qualifier("fileReadStatusRepository") ReadStatusRepository readStatusRepository) {
        this.messageRepository = messageRepository;
        this.readStatusRepository = readStatusRepository;

        this.DIRECTORY = Paths.get(System.getProperty("user.dir"),
                "file-data-map", Channel.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException("저장 디렉토리를 생성할 수 없습니다.", e);
            }
        }
    }


    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    //파일에서 채널 읽기
    private Optional<Channel> readChannelFile(Path path) {
        if (!Files.exists(path)) return Optional.empty();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))){
            return Optional.ofNullable((Channel) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            return Optional.empty(); //예외 발생 시 빈 값 반환
        }
    }

    private void saveChannelFile(Channel channel) {
        Path path = resolvePath(channel.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))){
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException("채널 정보를 저장하는 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public ChannelResponse createPublicChannel(ChannelCreateRequest request) {
        Channel channel = new Channel(request.name());
        saveChannelFile(channel);
        return convertToResponse(channel);
    }

    @Override
    public ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(null);
        saveChannelFile(channel);
        return convertToResponse(channel);
    }

    @Override
    public ChannelResponse findById(UUID id) {
        Channel channel = readChannelFile(resolvePath(id))
                .orElseThrow(() -> new NoSuchElementException("해당" + id + "채널을 찾을 수 없습니다."));
        return convertToResponse(channel);
    }

    @Override
    public List<ChannelResponse> findAll() {
        try (Stream<Path> paths = Files.list(DIRECTORY)){
            return paths
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(this::readChannelFile)
                    .flatMap(Optional::stream)
                    .map(this::convertToResponse)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("목록 조회 중 오류 발생", e);
        }
    }

    @Override
    public ChannelResponse update(UUID id, ChannelUpdateRequest request) {
        Channel channel = readChannelFile(resolvePath(id))
                .orElseThrow(() -> new NoSuchElementException("수정하려는 채널이 존재하지 않습니다."));

        channel.update(request.name());
        saveChannelFile(channel);

        return convertToResponse(channel);

    }

    @Override
    public void deleteById(UUID id) {
        Path path = resolvePath(id);
        if (Files.notExists(path)){
            throw new NoSuchElementException("삭제하려는 채널이 존재하지 않습니다.");
        }

        messageRepository.deleteAllByChannelId(id);
        readStatusRepository.deleteAllByChannelId(id);

        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException("채널 삭제 실패", e);
        }
    }
    private ChannelResponse convertToResponse(Channel channel){
        return new ChannelResponse(
                channel.getId(),
                channel.getName()
        );
    }

}
*/
