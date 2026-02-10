package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.DTO.*;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		// 이제 BasicService들이 자동으로 주입됩니다.
		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);
		AuthService authService = context.getBean(AuthService.class);

		System.out.println("\n ｡♥｡‧˚♡˚ ‧｡♥｡‧˚♡˚ ‧｡♥｡ TEST START ‧˚♡˚ ‧｡♥｡‧˚♡˚ ‧｡♥｡‧˚♡˚ ");

		UserResponse user = userCRUDTest(userService);
		ChannelResponse channel = channelCRUDTest(channelService);
		messageCRUDTest(messageService, channel, user);
		loginTest(authService);

		System.out.println("\n ｡♥｡‧˚♡˚ ‧｡♥｡‧˚♡˚ ‧｡♥｡ TEST END ‧˚♡˚ ‧｡♥｡‧˚♡˚ ‧｡♥｡‧˚♡˚ ");
	}

	private static UserResponse userCRUDTest(UserService userService) {
		System.out.println("\n=========== [1. 유저 기능 테스트] ===========");

		UserResponse user = userService.create(new UserCreateRequest(
				"김사연", "sayeon@gmail.com", "1234", "01044444444", null));
		System.out.println("1. 유저 생성 완료 : " + user.displayName() + " (ID: " + user.id() + ")");

		UserResponse foundUser = userService.findById(user.id());
		System.out.println("2. 유저 단건 조회 : " + foundUser.displayName() + " / 이메일: " + foundUser.email());

		List<UserResponse> foundUsers = userService.findAll();
		System.out.println("3. 유저 다건 조회 : 현재 총 " + foundUsers.size() + "명 등록됨");

		UserResponse updatedUser = userService.update(user.id(), new UserUpdateRequest("updated_사연", null, "5678", null, null));
		System.out.println("4. 유저 수정 완료 : " + user.displayName() + " -> " + updatedUser.displayName());

		userService.deleteById(user.id());
		System.out.println("5. 유저 삭제 완료 : " + user.id());

		return user;
	}

	private static ChannelResponse channelCRUDTest(ChannelService channelService) {
		System.out.println("\n=========== [2. 채널 기능 테스트] ===========");

		ChannelResponse channel = channelService.createPublicChannel(new ChannelCreateRequest("공지"));
		System.out.println("1. 채널 생성 완료 : " + channel.name() + " (ID: " + channel.id() + ")");

		ChannelResponse foundChannel = channelService.findById(channel.id());
		System.out.println("2. 채널 단건 조회 : " + foundChannel.name());

		ChannelResponse updatedChannel = channelService.update(channel.id(), new ChannelUpdateRequest("공지사항"));
		System.out.println("3. 채널 수정 완료 : " + channel.name() + " -> " + updatedChannel.name());

		channelService.deleteById(channel.id());
		System.out.println("4. 채널 삭제 완료 : " + channel.id());

		return channel;
	}

	private static void messageCRUDTest(MessageService messageService, ChannelResponse channel, UserResponse user) {
		System.out.println("\n=========== [3. 메세지 기능 테스트] ===========");

		MessageCreateRequest request = new MessageCreateRequest(
				"안녕하세요! 첫 메세지입니다.",
				user.id(),
				channel.id(),
				null
		);
		MessageResponse message = messageService.create(request);
		System.out.println("1. 메세지 생성함 : [작성자: " + user.displayName() + "] " + message.content());

		MessageResponse foundMessage = messageService.findById(message.id());
		System.out.println("2. 메세지 조회됨 : " + foundMessage.content() + " (ID: " + foundMessage.id() + ")");

		messageService.deleteById(message.id());
		System.out.println("3. 메세지 삭제함 : " + message.id());
	}

	private static void loginTest(AuthService authService) {
		System.out.println("\n=========== [4. 로그인 기능 테스트] ===========");
		try {
			// 아까 만든 유저(sayeon@gmail.com / 1234)로 로그인 시도
			LoginRequest loginRequest = new LoginRequest("sayeon@gmail.com", "1234");
			UserResponse loggedInUser = authService.login(loginRequest);
			System.out.println("1. 로그인 성공! 환영합니다, " + loggedInUser.displayName() + "님.");
		} catch (Exception e) {
			System.out.println("1. 로그인 실패: " + e.getMessage());
		}

		try {
			// 틀린 비밀번호로 로그인 시도
			LoginRequest badRequest = new LoginRequest("sayeon@gmail.com", "0000");
			authService.login(badRequest);
		} catch (Exception e) {
			System.out.println("2. 로그인 실패 (예상된 결과): " + e.getMessage());
		}
	}
}