package service.jcf;

import entity.Channel;
import service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> data;

    public JCFChannelService() {
        data = new ArrayList<>();

        List<Channel> channels = List.of(
            new Channel("행정-공지"),
            new Channel("학습-공지"),
            new Channel("오늘-뭐먹지"),
            new Channel("소개합니다"),
            new Channel("추억-쌓아요")
        );
        data.addAll(channels);
    }
    @Override
    public Channel create(String name){
        Channel channel = new Channel(name);
        data.add(channel);
        return true;
    }

    @Override
    public Channel findById(UUID id){
        for (Channel channel : data){
            if (channel.getId().equals(id)){
                return channel;
            }
        }return null;
    }

    @Override
    public List<Channel> findAll(){
        return data;
    }

    @Override
    public Channel update(UUID id, String name) {
        Channel channel = findById(id);
        if (channel == null) {
            return false;
        }
        channel.update(name);
        return true;
    }

    @Override
    public boolean delete(UUID id) {
        Channel channel = findById(id);
        if (channel == null) {
            return false;
        }
        data.remove(channel);
        return true;
    }


}
