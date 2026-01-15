package service;

import entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(String displayName, String email, String phoneNumber);
    User findById(UUID id);
    List<User> findAll();
    User update(UUID id, String displayName, String email, String phoneNumber);
    //삭제 - 가져온 유저 안에 필드인 아이디를 조회해서 리스트에서 검색한다음 삭제하도록 진행
    boolean delete(UUID id);
}

