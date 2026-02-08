package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.MessageCreateRequest;
import com.sprint.mission.discodeit.DTO.MessageResponse;
import com.sprint.mission.discodeit.DTO.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final BinaryContentService binaryContentService;


    @Override
    public MessageResponse create(MessageCreateRequest request){
        List<UUID> attachmentIds = new ArrayList<>();
        if (request.attachments() != null && !request.attachments().isEmpty()){
            attachmentIds = request.attachments().stream()
                    .map(binaryContentService::create)
                    .map(response -> response.id())
                    .collect(Collectors.toList());
        }

        Message message = new Message(
                request.content(),
                request.userId(),
                request.channelId()
        );

        attachmentIds.forEach(message::addAttachmentId);

        Message savedMessage = messageRepository.save(message);

        if (savedMessage == null){
            throw new RuntimeException("메세지 생성에 실패했습니다.");
        }
        return convertToResponse(savedMessage);
    }
    @Override
    public MessageResponse findById(UUID id){
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("메세지 조회를 실패했습니다."));

        return convertToResponse(message);
    }

    @Override
    public List<MessageResponse> findAll() {
        return messageRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public MessageResponse update(UUID id, MessageUpdateRequest request){
        Message message = messageRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("메세지를 찾을 수 없습니다."));

        message.update(request.content()); //메세지 내용만 바뀌는거니까
        Message updatedMessage = messageRepository.save(message);

        return convertToResponse(updatedMessage);
    }

    @Override
    public void deleteById(UUID id){
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("삭제하려는 메세지를 찾을 수 없습니다."));

        if (message.getAttachmentIds() != null) {
            message.getAttachmentIds().forEach(binaryContentService::deleteById);
        }
        messageRepository.deleteById(id);
    }

    private MessageResponse convertToResponse(Message message){
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
