package service.jcf;

import entity.User;
import service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final List<User> data;

    public JCFUserService(){
        data = new ArrayList<>();

        List<User> users = List.of( // 자기 자신 수정 불가, 다른 리스트에 전달 가능
                new User("김사연", "yonnzzang@gmail.com","010-5543-9943"),
                new User("박지은", "jieun@gmail.com","010-1234-1234"),
                new User("강지원", "jiwon@gmail.com","010-2345-2345"),
                new User("육선우", "sunwoo@gmail.com","010-3456-3456"),
                new User("이진용", "jinyong@gmail.com", "010-4567-4567")
        );
        data.addAll(users); //users 안에 있는 요소를 꺼내서 data안에 복사 ->users 는 안건드림

    }

    @Override
    public User create(String displayName, String email, String phoneNumber){
        User user = new User(displayName, email, phoneNumber);
        data.add(user);
        return user;
    }
    @Override
    public User findById(UUID id){
        for (User user : data) {
            if (user.getId().equals(id)){
                return user;
            }
        }
        return null;  //stream api
    }

    @Override
    public List<User> findAll(){
        return new ArrayList<>(data);
    }

    @Override
    public User update(UUID id, String displayName, String email, String phoneNumber){
        User user = findById(id);
        if (user != null) {
            user.update(displayName, email, phoneNumber);
        }
        return user;
    }

    @Override
    public boolean delete(UUID id) { // boolean 을 쓴 이유 -> 결과가 중요해서!!
        User user = findById(id);
        if (user == null) return false; //실패시 메세지출력
        return data.remove(user);

/*        if (user != null){
            return date.remove(user);
        }
        return false;*/
    }

}



