package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {

    private static final String FILE_PATH = "messages.dat";
    private Map<UUID, Message> data = new HashMap<>();

    // 생성자
    public FileMessageRepository() {
        loadFromFile();
    }

    @Override
    public Message save(Message message) {
        data.put(message.getId(), message);
        saveToFile();
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
        saveToFile();
    }

    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }

    // update 필요하면 그대로 두고 @Override 제거
    public Message update(Message message) {
        data.put(message.getId(), message);
        saveToFile();
        return message;
    }

    // 파일 읽기
    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Map<UUID, Message> loaded = (Map<UUID, Message>) ois.readObject();
            data.clear();
            data.putAll(loaded);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 파일 저장
    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
