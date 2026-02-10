package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.MessageCreateRequest;
import com.sprint.mission.discodeit.DTO.MessageResponse;
import com.sprint.mission.discodeit.DTO.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final BinaryContentService binaryContentService;

    @Override
    public MessageResponse create(MessageCreateRequest request) {
        Message message = new Message(
                request.content(),
                request.userId(),
                request.channelId()
        );

        // 보내주신 첨부파일 로직 유지
        if(request.attachments() != null) {
            request.attachments().stream()
                    .map(binaryContentService::create)
                    .forEach(response -> message.addAttachmentId(response.id()));
        }

        messageRepository.save(message);
        return convertToResponse(message);
    }

    @Override
    public MessageResponse findById(UUID id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(id + " 메세지를 찾을 수 없습니다."));
        return convertToResponse(message);
    }

    @Override
    public List<MessageResponse> findAll() {
        return messageRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public MessageResponse update(UUID id, MessageUpdateRequest request) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("수정하려는 메세지가 존재하지 않습니다."));

        // 내용 업데이트
        message.update(request.content());

        // 저장
        messageRepository.save(message);

        return convertToResponse(message);
    }

    @Override
    public void deleteById(UUID id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("삭제하려는 메세지가 존재하지 않습니다."));

        // 보내주신 첨부파일 삭제 로직 유지
        if (message.getAttachmentIds() != null) {
            message.getAttachmentIds().forEach(binaryContentService::deleteById);
        }

        // 메시지 삭제
        messageRepository.deleteById(id);
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