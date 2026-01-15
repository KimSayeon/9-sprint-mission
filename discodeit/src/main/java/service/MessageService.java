package service;

import entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(String content, UUID userId, UUID channelId);
    Message findById(UUID id);
    List<Message> findAll();
    Message update(UUID id, String content);
    boolean delete(UUID id);

}
