package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.ChannelCreateRequest;
import com.sprint.mission.discodeit.DTO.ChannelResponse;
import com.sprint.mission.discodeit.DTO.ChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class BasicChannelService implements ChannelService{

    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public ChannelResponse create(ChannelCreateRequest request) {
        Channel channel = new Channel(request.name());
        Channel savedChannel = channelRepository.save(channel);

        if (savedChannel == null) {
            throw new RuntimeException("채널 생성에 실패했습니다. 입력 정보를 다시 확인해주세요.");
        } //엔티티를 response 가방에 담아서 내보내기
        return new ChannelResponse(savedChannel.getId(), savedChannel.getName());
    }

    @Override
    public ChannelResponse findById(UUID id){
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("조회 실패: ID(" + id + ") 유저를 찾을 수 없습니다."));

        return new ChannelResponse(channel.getId(), channel.getName());
    }

    @Override
    public List<ChannelResponse> findAll(){
        return channelRepository.findAll().stream()
                .map(channel -> new ChannelResponse(
                        channel.getId(),
                        channel.getName()
                ))
                .toList();
    }

    @Override
    public ChannelResponse update(UUID id, ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("수정 실패: ID(" + id + ") 유저를 찾을 수 없습니다."));

        channel.update(request.name());

        Channel updateChannel = channelRepository.save(channel);

        return new ChannelResponse(updateChannel.getId(), updateChannel.getName());
    }

    @Override
    public void deleteById(UUID id) {
        Channel channel = channelRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("삭제 실패: ID(" + id + ") 채널을 찾을 수 없습니다."));
        messageRepository.deleteAllByChannelId(id);
        readStatusRepository.deleteAllByChannelId(id);
        channelRepository.deleteById(id);
    }

}
