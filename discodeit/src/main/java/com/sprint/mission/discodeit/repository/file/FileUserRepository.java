package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
public class FileUserRepository implements UserRepository {
    private static final String FILE_PATH = "users.dat";
    private Map<UUID, User> data = new HashMap<>();

    public FileUserRepository(){
        loadFromFile();
    }

    @Override
    public User save(User user){
        data.put(user.getId(), user);
        saveToFile();
        return user;
    }
    @Override
    public Optional<User> findById(UUID id){
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<User> findAll(){
        return new ArrayList<>(data.values());
    }

    public User update(User user){
        data.put(user.getId(),user);
        saveToFile();
        return user;
    }

    @Override
    public void deleteById(UUID id){
        if (data.containsKey(id)){
            data.remove(id);
            saveToFile();
        }
    }

    private void loadFromFile(){
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
            data = (Map<UUID, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("데이터 로드 중 오류 발생: " + e.getMessage());
        }
    }

    private void saveToFile(){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))){
            oos.writeObject(data);
        } catch (IOException e) {
            System.out.println("데이터 저장 중 오류 발생: " + e.getMessage());

        }
    }

    @Override
    public Optional<User> findByDisplayName(String displayName) {
        return findAll().stream()
                .filter(user -> displayName.equals(user.getDisplayName()))
                .findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return findAll().stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst();
    }

}
