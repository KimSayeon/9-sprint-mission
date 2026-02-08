package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.DTO.MessageCreateRequest;
import com.sprint.mission.discodeit.DTO.MessageResponse;
import com.sprint.mission.discodeit.DTO.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
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
public class FileMessageService implements MessageService {

    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";
    private final BinaryContentService binaryContentService;

    public FileMessageService(BinaryContentService binaryContentService){
        this.binaryContentService = binaryContentService;
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", Message.class.getSimpleName());
        if (Files.notExists(DIRECTORY)){
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException("저장 디렉토리를 생성할 수 없습니다.", e);
            }
        }

    }

    private Path resolvePath(UUID id) {return DIRECTORY.resolve(id + EXTENSION);}

    private Optional<Message> readMessageFile(Path path) {
        if (!Files.exists(path)) return Optional.empty();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))){
            return Optional.ofNullable((Message) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    private void saveMessageFile(Message message){
        Path path = resolvePath(message.getId());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path.toFile()))){
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException("메세지 정보를 저장하는 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public MessageResponse create(MessageCreateRequest request) {
        Message message = new Message(
                request.content(),
                request.userId(),
                request.channelId()
        );

        if(request.attachments() != null) {
            request.attachments().stream()
                    .map(binaryContentService::create)
                    .forEach(response -> message.addAttachmentId(response.id()));
        }

        saveMessageFile(message);
        return convertToResponse(message);
    }

    @Override
    public MessageResponse findById(UUID id) {
        Message messageNullable = readMessageFile(resolvePath(id))
                .orElseThrow(() -> new NoSuchElementException(id + "메세지를 찾을 수 없습니다."));
        return convertToResponse(messageNullable);
    }

    @Override
    public List<MessageResponse> findAll() {
        try (Stream<Path> paths = Files.list(DIRECTORY)){
            return paths
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(this::readMessageFile)
                    .flatMap(Optional::stream)
                    .map(this::convertToResponse)
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("전체 메세지 목록을 읽어오는 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public MessageResponse update(UUID id, MessageUpdateRequest request) {
        Message message = readMessageFile(resolvePath(id))
                .orElseThrow(() -> new NoSuchElementException("수정하려는 메세지가 존재하지 않습니다."));
        message.update(request.content());
        saveMessageFile(message);

        return convertToResponse(message);
    }

    @Override
    public void deleteById(UUID id) {
        Message message = readMessageFile(resolvePath(id))
                .orElseThrow(()-> new NoSuchElementException("삭제하려는 메세지가 존재하지 않습니다."));

        if (message.getAttachmentIds() != null) {
            message.getAttachmentIds().forEach(binaryContentService::deleteById);
        }
        try {
            Files.delete(resolvePath(id));
        } catch (IOException e) {
            throw new RuntimeException("메세지 삭제 실패: "+ id, e);
        }
    }
    private MessageResponse convertToResponse(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getContent(),
                message.getUserId(),
                message.getChannelId(),
                message.getAttachmentIds(),
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }
}
