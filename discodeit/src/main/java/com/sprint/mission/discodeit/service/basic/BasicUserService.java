package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicUserService implements UserService {

    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public User create(String name, String email, String password){
        User user = new User(name, email, password);
        return userRepository.save(user);
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(UUID id, String name, String email, String password) {
        User user = findById(id);
        user.update(name, email, password);
        return userRepository.save(user);
    }
    //    public User save(User user) {
//        return userRepository.save(user);
//    }

    @Override
    public void delete(UUID id) {
        userRepository.deleteById(id);
    }

}
