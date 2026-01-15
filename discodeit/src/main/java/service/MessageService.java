package service;

import entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    boolean create(String content, UUID userId, UUID channelId);
    Message findById(UUID id);
    List<Message> findAll();
    boolean update(UUID id, String content);
    boolean delete(UUID id);

}
