package com.sprint.mission.discodeit.service.basic;


import com.sprint.mission.discodeit.DTO.*;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
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
    public ChannelResponse createPublicChannel(ChannelCreateRequest request) {

        Channel channel = new Channel(request.name());
        Channel savedChannel = channelRepository.save(channel);

        if (savedChannel == null) {
            throw new RuntimeException("PUBLIC 채널 생성에 실패했습니다.");
        }
        return convertToResponse(savedChannel);
    }

    @Override
    public ChannelResponse createPrivateChannel(PrivateChannelCreateRequest request) {
        Channel channel = new Channel(null);
        Channel savedChannel = channelRepository.save(channel);

        if (savedChannel == null){
            throw new RuntimeException("PRIVATE 채널 생성에 실패했습니다.");
        }
        request.userIds().forEach(userId -> {
            ReadStatus readStatus = new ReadStatus(userId, savedChannel.getId());
            readStatusRepository.save(readStatus);
        });

        return convertToResponse(savedChannel);
    }

    @Override
    public ChannelResponse findById(UUID id){
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("채널 조회를 실패했습니다."));

        return convertToResponse(channel);
    }

    @Override
    public List<ChannelResponse> findAll(){
        return channelRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public ChannelResponse update(UUID id, ChannelUpdateRequest request) {
        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다."));

        channel.update(request.name());
        Channel updatedChannel = channelRepository.save(channel);

        return convertToResponse(updatedChannel);
    }

    @Override
    public void deleteById(UUID id) {
        channelRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("삭제하려는 채널을 찾을 수 없습니다."));

        messageRepository.deleteAllByChannelId(id);
        readStatusRepository.deleteAllByChannelId(id);
        channelRepository.deleteById(id);
    }

    private ChannelResponse convertToResponse(Channel channel) {
        return new ChannelResponse(
                channel.getId(),
                channel.getName()
        );
    }

}
