package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;

    public BasicMessageService(MessageRepository messageRepository){
        this.messageRepository = messageRepository;
    }

    @Override
    public Message create(String content, UUID id, UUID channelId){
        Message message = new Message(content, id, channelId);
        return messageRepository.save(message);
    }
    @Override
    public Message findById(UUID id){
        return messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));
    }

    @Override
    public List<Message> findAll() {return messageRepository.findAll();}

    @Override
    public Message update(UUID id, String content){
        Message message = findById(id);
        message.update(content);
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID id){ messageRepository.deleteById(id);}
}
