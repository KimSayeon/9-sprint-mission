import entity.User;
import service.ChannelService;
import service.UserService;
import service.jcf.JCFChannelService;
import service.jcf.JCFMessageService;
import service.jcf.JCFUserService;

public class JavaApplication {
    public static void main(String[] args) {
    //서비스 초기화
    JCFUserService userService = new JCFUserService();
    JCFChannelService channelService = new JCFChannelService();
    //CRUD 테스트 호출
    userCRUDTest(userService);
    channelCRUDTest(channelService);

    }
    static void userCRUDTest(UserService userService){
    User user = userService.create("김사연","4yonnzzang@gmail.com","010-5543-9943");



    }

    static void channelCRUDTest(ChannelService channelService){

    }
}
