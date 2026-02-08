package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.DTO.*;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
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

		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		System.out.println("\n ｡♥｡‧˚♡˚ ‧｡♥｡‧˚♡˚ ‧｡♥｡START‧˚♡˚ ‧｡♥｡‧˚♡˚ ‧｡♥｡‧˚♡˚ ");

		UserResponse user = userCRUDTest(userService);
		ChannelResponse channel = channelCRUDTest(channelService);
		messageCRUDTest(messageService, channel, user);

		System.out.println("\n ｡♥｡‧˚♡˚ ‧｡♥｡‧˚♡˚ ‧｡♥｡END‧˚♡˚ ‧｡♥｡‧˚♡˚ ‧｡♥｡‧˚♡˚ ");
	}

	private static UserResponse userCRUDTest(UserService userService){
		System.out.println("\n [유저 테스트]");

		UserResponse user = userService.create(new UserCreateRequest(
				"김사연","sayeon@gmail.com","1234","01044444444",null));
		System.out.println("유저 생성: " + user.id());

		UserResponse foundUser = userService.findById(user.id());
		System.out.println("유저 단건 조회: " + foundUser.displayName());

		List<UserResponse> foundUsers = userService.findAll();
		System.out.println("유저 다건 조회: " + foundUsers.size() + "명");

		UserResponse updatedUser = userService.update(user.id(), new UserUpdateRequest("updated_사연",null,"5678",null,null));
		System.out.println("유저 수정: " +updatedUser.displayName());

		userService.deleteById(user.id());
		System.out.println("유저 삭제 완료 !");

		return user;

	}
	private static ChannelResponse channelCRUDTest(ChannelService channelService){
		System.out.println("\n[채널 테스트]");
		ChannelResponse channel = channelService.createPublicChannel(new ChannelCreateRequest("공지"));
		System.out.println("채널 생성: " + channel.id());

		ChannelResponse foundChannel = channelService.findById(channel.id());
		System.out.println("채널 단건 조회: " + foundChannel.name());

		ChannelResponse updatedChannel = channelService.update(channel.id(), new ChannelUpdateRequest("공지사항"));
		System.out.println("채널 수정: " + updatedChannel.name());

		channelService.deleteById(channel.id());
		System.out.println("채널 삭제 완료 !");

		return channel;
	}

	private static MessageResponse messageCRUDTest(MessageService messageService, ChannelResponse channel, UserResponse user){
		System.out.println("\n [메세지 & 삭제 테스트]");

		MessageCreateRequest request = new MessageCreateRequest(
				"안녕하세요! 첫 메세지입니다.",
				user.id(),
				channel.id(),
				null
		);
		MessageResponse message = messageService.create(request);
		System.out.println("메세지 생성 완료: " + message.content());

		MessageResponse foundMessage = messageService.findById(message.id());
		System.out.println("메세지 조회: " + foundMessage.content());

		messageService.deleteById(message.id());
		System.out.println("메세지 삭제 완료 !");

		return message;

	}

}
