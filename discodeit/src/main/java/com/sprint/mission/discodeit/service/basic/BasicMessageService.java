package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;

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
    public void deleteById(UUID id){ messageRepository.deleteById(id);}
}
