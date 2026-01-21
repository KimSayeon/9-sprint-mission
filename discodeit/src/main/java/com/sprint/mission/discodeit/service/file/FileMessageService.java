package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class FileMessageService implements MessageService {

    private final ChannelService channelService;
    private final UserService userService;

    public FileMessageService(ChannelService channelService, UserService userService){
        this.channelService = channelService;
        this.userService = userService;

        if (Files.notExists(directory)){
            try {
                Files.createDirectories(directory);
            }catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static final String DIRECTORY = "data/messages";
    private static final String EXTENSION = ".dat";
    private final Path directory = Paths.get(DIRECTORY);

    private Path resolvePath(UUID id) {return directory.resolve(id + EXTENSION);}

    @Override
    public Message create(String content, UUID id, UUID channelId) {
        Message message = new Message(content, id, channelId);
        Path path = resolvePath(message.getId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return message;
    }

    @Override
    public Message findById(UUID id) {
        Message messageNullable = null;
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                messageNullable = (Message) ois.readObject();
            }catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.ofNullable(messageNullable)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + id + " not found"));
    }

    @Override
    public List<Message> findAll() {
        try {
            return Files.list(Paths.get(DIRECTORY))
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (Message) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Message update(UUID id, String content) {
        Message messageNullable = null;
        Path path = resolvePath(id);
        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ){
                messageNullable = (Message) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        Message message = Optional.ofNullable(messageNullable)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + id + " not found"));
        message.update(content);

        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return message;
    }

    @Override
    public void delete(UUID id) {
        Path path = resolvePath(id);
        if (Files.notExists(path)) {
            throw new NoSuchElementException("Message with id " + id + " not found");
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
