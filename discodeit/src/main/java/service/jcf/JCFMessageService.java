package service.jcf;

import entity.Message;
import service.MessageService;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import entity.User;
import entity.Channel;
import service.ChannelService;
import service.UserService;

public class JCFMessageService implements MessageService {
    private final List<Message> data;

    public JCFMessageService(){
        data = new ArrayList<>();

        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();

        List<User> users = userService.findAll();
        List<Channel> channels = channelService.findAll();

        List<Message> messages = List.of(
            new Message("좋아하는 음식: 딱히 없음",users.get(0).getId(),channels.get(2).getId()),
            new Message("좋아하는 음식: 두쫀쿠",users.get(2).getId(),channels.get(4).getId()),
            new Message("음식 사진",users.get(3).getId(),channels.get(3).getId()),
            new Message("오늘 날씨 -5도..",users.get(4).getId(),channels.get(5).getId()),
            new Message("점심 사진",users.get(5).getId(),channels.get(3).getId())
        );
        data.addAll(messages);
    }
}
