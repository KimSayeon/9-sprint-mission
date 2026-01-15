import entity.Channel;
import entity.Message;
import entity.User;
import service.ChannelService;
import service.MessageService;
import service.UserService;
import service.jcf.JCFChannelService;
//import service.jcf.JCFMessageService;
import service.jcf.JCFMessageService;
import service.jcf.JCFUserService;

import java.util.List;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
    //서비스 초기화
    UserService userService = new JCFUserService();
    ChannelService channelService = new JCFChannelService();
    MessageService messageService = new JCFMessageService();
    //CRUD 테스트 호출
    userCRUDTest(userService);
    channelCRUDTest(channelService);
    messageCRUDTest(messageService);

    }
    static void userCRUDTest(UserService userService){
        //생성
        User user = userService.create("김사연","4yonnzzang@gmail.com","010-5543-9943");
        System.out.println("유저 생성: " + user.getId());
        //조회 단건
        User foundUser = userService.findById(user.getId());
        System.out.println("유저 조회(단건): " + foundUser.getDisplayName());
        //조회 다건
        List<User> foundUsers = userService.findAll();
        System.out.println("유저 조회(다건) 수: " + foundUsers.size());
        //수정
        User updatedUser = userService.update(user.getId(), "사연수정", null, null);
        System.out.println("유저 수정: " + updatedUser.getDisplayName() + "/" + updatedUser.getEmail() + "/" + updatedUser.getPhoneNumber());
        //삭제
        boolean deleted = userService.delete(user.getId());
        System.out.println("유저 삭제 성공: " + deleted);
        //삭제 후 조회
        System.out.println("유저 조회 후 수: " + userService.findAll().size());

    }

    static void channelCRUDTest(ChannelService channelService){
        //생성
        Channel channel = channelService.create("소개합니다");
        System.out.println("채널 생성: " + channel.getId());
        //조회 단건
        Channel foundChannel = channelService.findById(channel.getId());
        System.out.println("채널 조회(단건): " + foundChannel.getName());
        //조회 다건
        List<Channel> foundChannels = channelService.findAll();
        System.out.println("채널 조회(다건) 수: " + foundChannels.size());
        //수정
        Channel updatedChannel = channelService.update(channel.getId(), "채널수정");
        System.out.println("채널 수정: " + updatedChannel.getName());
        //삭제
        boolean deleted = channelService.delete(channel.getId());
        System.out.println("채널 삭제 성공: " + deleted);
        //삭제 후 조회
        System.out.println("채널 조회 후 수: " + channelService.findAll().size());

    }

    static void messageCRUDTest(MessageService messageService){
        UUID userid = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        Message message = messageService.create("좋아하는 음식: 딱히 없음",userid, channelId);
        System.out.println("메세지 생성: " + message.getId());

        Message foundMessage = messageService.findById(message.getId());
        System.out.println("메세지 조회(단건): " + foundMessage.getId());

        List<Message> foundMessages = messageService.findAll();
        System.out.println("메세지 조회(다건): " + foundMessages.size());

        Message updatedMessage = messageService.update(message.getId(), "좋아하는 음식: 전부 다!");
        System.out.println("메세지 수정: " + updatedMessage.getContent());

        boolean deleted = messageService.delete(message.getId());
        System.out.println("메세지 삭제 성공: " + deleted);


    }
}
