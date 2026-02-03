package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

public class JCFMessageService implements MessageService {
    private final List<Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService){
        this.userService = userService;
        this.channelService = channelService;
        data = new ArrayList<>(); //Message를 저장할 빈 통 준비

        List<User> users = userService.findAll();
        List<Channel> channels = channelService.findAll();
        //현재 존재하는 모든 유저,채널을 리스트로 가져옴 , 메세지 만들 때 필요


        List<Message> messages = List.of(
            new Message("좋아하는 음식: 딱히 없음",users.get(0).getId(),channels.get(3).getId()),
            new Message("좋아하는 음식: 두쫀쿠",users.get(1).getId(),channels.get(3).getId()),
            new Message("음식 사진",users.get(2).getId(),channels.get(2).getId()),
            new Message("오늘 날씨 -5도..",users.get(3).getId(),channels.get(4).getId()),
            new Message("점심 사진",users.get(4).getId(),channels.get(2).getId())
        );
        data.addAll(messages);
    }
    public Message create(String content, UUID userId, UUID channelId){
        Message message = new Message(content, userId, channelId);
        data.add(message);
        return message;
    }
    public Message findById(UUID id){
        for (Message message : data) {
            if (message.getId().equals(id)){
                return message;
            }
        }
        return null;
    }

    public List<Message> findAll(){
        return new ArrayList<>(data);
    }   //서비스 내부 상태(data)를 외부에서 변경하지 못하게 하려고 new Array
        // data는 JCF가 관리하는 내부 저장소

    public Message update(UUID id, String content){
        Message message = findById(id);
        if (message != null){
            message.update(content);
        }
        return message;
    }

    public void deleteById(UUID id){
        Message message = findById(id);
        if (message == null){
            throw new NoSuchElementException("Message not found");
        }
        data.remove(message);
    }
}
