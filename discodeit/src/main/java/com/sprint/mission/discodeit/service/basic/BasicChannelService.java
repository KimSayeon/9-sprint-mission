package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.ChannelCreateRequest;
import com.sprint.mission.discodeit.DTO.ChannelResponse;
import com.sprint.mission.discodeit.DTO.ChannelUpdateRequest;
import com.sprint.mission.discodeit.DTO.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public ChannelResponse createPublicChannel(ChannelCreateRequest request) {
        Channel channel = new Channel(request.name());
        channelRepository.save(channel);
        return convertToResponse(channel);
    }

    @Override
    public ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request) {
        // 1. Private 채널 생성 (이름 없음)
        Channel channel = new Channel(null);
        channelRepository.save(channel);

        // 2. [누락 기능 구현] 참여하는 멤버들의 ReadStatus 생성
        // (PrivateChannelCreateRequest에 memberIds가 있다고 가정)
        if (request.memberIds() != null) {
            for (UUID memberId : request.memberIds()) {
                // ReadStatus 생성자: (userId, channelId)
                // ※ ReadStatus 엔티티 생성자에 맞춰야 합니다.
                // 혹시 생성자가 다르면 ReadStatus 파일 확인 후 수정해주세요!
                ReadStatus readStatus = new ReadStatus(memberId, channel.getId());
                readStatusRepository.save(readStatus);
            }
        }

        return convertToResponse(channel);
    }

    @Override
    public ChannelResponse findById(UUID id) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 " + id + " 채널을 찾을 수 없습니다."));
        return convertToResponse(channel);
    }

    // [수정] findAll -> findAllByUserId (인터페이스 요구사항 반영)
    @Override
    public List<ChannelResponse> findAllByUserId(UUID userId) {
        return channelRepository.findAll().stream()
                .filter(channel -> isAccessible(channel, userId)) // 접근 권한 필터링
                .map(this::convertToResponse)
                .toList();
    }

    // [헬퍼 메소드] 유저가 채널을 볼 수 있는지 확인
    private boolean isAccessible(Channel channel, UUID userId) {
        // 1. 공개 채널(이름이 있음)이면 누구나 볼 수 있음
        if (channel.getName() != null) {
            return true;
        }
        // 2. 비공개 채널이면 ReadStatus(참여 정보)가 있어야 함
        // (ReadStatusRepository에 findByChannelIdAndUserId가 없다면 findAll로 걸러야 함)
        return readStatusRepository.findAll().stream()
                .anyMatch(status -> status.getChannelId().equals(channel.getId())
                        && status.getUserId().equals(userId));
    }

    @Override
    public ChannelResponse update(UUID id, ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("수정하려는 채널이 존재하지 않습니다."));

        // [누락 기능 구현] PRIVATE 채널은 수정 불가
        if (channel.getName() == null) {
            throw new IllegalArgumentException("비공개(PRIVATE) 채널은 수정할 수 없습니다.");
        }

        channel.update(request.name());
        channelRepository.save(channel);

        return convertToResponse(channel);
    }

    @Override
    public void deleteById(UUID id) {
        if (channelRepository.findById(id).isEmpty()){
            throw new NoSuchElementException("삭제하려는 채널이 존재하지 않습니다.");
        }

        messageRepository.deleteAllByChannelId(id);
        readStatusRepository.deleteAllByChannelId(id);
        channelRepository.deleteById(id);
    }

    // BasicChannelService.java의 마지막 메소드
    private ChannelResponse convertToResponse(Channel channel){
        // 1. 마지막 메시지 시간 구하기 (로직은 아까와 동일)
        Instant lastMessageAt = messageRepository.findAll().stream()
                .filter(msg -> msg.getChannelId().equals(channel.getId()))
                .max(Comparator.comparing(Message::getCreatedAt))
                .map(Message::getCreatedAt)
                .orElse(null);

        // 2. 참여자 ID 목록 구하기 (로직은 아까와 동일)
        List<UUID> participantIds = null; // 변수명 participantIds로 통일
        if (channel.getName() == null) {
            // ReadStatusRepository에서 해당 채널의 userId들을 가져옴
            // (※ ReadStatus에 findByChannelId 같은 게 없다면 findAll() 필터링)
            participantIds = readStatusRepository.findAll().stream()
                    .filter(status -> status.getChannelId().equals(channel.getId()))
                    .map(ReadStatus::getUserId) // getUserId()가 맞는지 확인 필요 (필드명에 따라 다름)
                    .toList();
        }

        return new ChannelResponse(
                channel.getId(),
                channel.getName(),
                lastMessageAt,   // 시간
                participantIds   // 참여자 목록
        );
    }
}