package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService{

    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel create(String name) {
        Channel channel = new Channel(name);
        return channelRepository.save(channel);
    }

    @Override
    public Channel findById(UUID id){
        return channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found"));
    }

    @Override
    public List<Channel> findAll(){
        return channelRepository.findAll();
    }

    @Override
    public Channel update(UUID id, String name) {
        Channel channel = findById(id);
        channel.update(name);
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID id) {
        channelRepository.deleteById(id);
    }

}
