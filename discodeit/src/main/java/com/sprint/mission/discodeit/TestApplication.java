package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;


import java.util.List;
import java.util.UUID;

public class TestApplication {
    static void userCRUDTest(UserService userService) {
        // 생성
        User user = userService.create("woody", "woody@codeit.com", "woody1234");
        System.out.println("유저 생성: " + user.getId());
        // 조회
        User foundUser = userService.findById(user.getId());
        System.out.println("유저 조회(단건): " + foundUser.getId());
        List<User> foundUsers = userService.findAll();
        System.out.println("유저 조회(다건): " + foundUsers.size());
        // 수정
        User updatedUser = userService.update(user.getId(), null, null, "woody5678");
        System.out.println("유저 수정: " + String.join("/", updatedUser.getDisplayName(), updatedUser.getEmail(), updatedUser.getPhoneNumber()));
        // 삭제
        userService.delete(user.getId());
        List<User> foundUsersAfterDelete = userService.findAll();
        System.out.println("유저 삭제: " + foundUsersAfterDelete.size());
    }

    static void channelCRUDTest(ChannelService channelService) {
        // 생성
        Channel channel = channelService.create("공지");
        System.out.println("채널 생성: " + channel.getId());
        // 조회
        Channel foundChannel = channelService.findById(channel.getId());
        System.out.println("채널 조회(단건): " + foundChannel.getId());
        List<Channel> foundChannels = channelService.findAll();
        System.out.println("채널 조회(다건): " + foundChannels.size());
        // 수정
        Channel updatedChannel = channelService.update(channel.getId(), "공지사항");
        System.out.println("채널 수정: " + String.join("/", updatedChannel.getName()));
        // 삭제
        channelService.delete(channel.getId());
        List<Channel> foundChannelsAfterDelete = channelService.findAll();
        System.out.println("채널 삭제: " + foundChannelsAfterDelete.size());
    }

    static void messageCRUDTest(MessageService messageService) {
        // 생성
        UUID channelId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Message message = messageService.create("안녕하세요.", channelId, id);  //????
        System.out.println("메시지 생성: " + message.getId());
        // 조회
        Message foundMessage = messageService.findById(message.getId());
        System.out.println("메시지 조회(단건): " + foundMessage.getId());
        List<Message> foundMessages = messageService.findAll();
        System.out.println("메시지 조회(다건): " + foundMessages.size());
        // 수정
        Message updatedMessage = messageService.update(message.getId(), "반갑습니다.");
        System.out.println("메시지 수정: " + updatedMessage.getContent());
        // 삭재
        messageService.delete(message.getId());
        List<Message> foundMessagesAfterDelete = messageService.findAll();
        System.out.println("메시지 삭제: " + foundMessagesAfterDelete.size());
    }

    static User setupUser(UserService userService) {
        User user = userService.create("woody", "woody@codeit.com", "woody1234");
        return user;
    }

    static Channel setupChannel(ChannelService channelService) {
        Channel channel = channelService.create("공지");
        return channel;
    }

    static void messageCreateTest(MessageService messageService, String content, User user, Channel channel) {
        Message message = messageService.create("안녕하세요.", user.getId(), channel.getId());
        System.out.println("메시지 생성: " + message.getId());
    }

    public static void main(String[] args) {
        // 서비스 초기화
        UserService userService = new FileUserService();
        ChannelService channelService = new FileChannelService();
        MessageService messageService = new FileMessageService(channelService, userService);

        //테스트
        userCRUDTest(userService);
        channelCRUDTest(channelService);
        messageCRUDTest(messageService);

        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        // 테스트
        String content = "안녕하세요.";
        messageCreateTest(messageService, content, user, channel);
    }
}