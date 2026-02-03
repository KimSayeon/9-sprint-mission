package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
public class FileChannelRepository implements ChannelRepository {
    private static final String FILE_PATH = "channels.dat";
    private Map<UUID, Channel> data = new HashMap<>();

    public FileChannelRepository(){loadFromFile();}

    @Override
    public Channel save(Channel channel){
        data.put(channel.getId(), channel);
        saveToFile();
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id){
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Channel> findAll() {return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID targetId){
        if (data.containsKey(targetId)){
            data.remove(targetId);
            saveToFile();
        }
    }

    public Channel update(Channel channel){
        data.put(channel.getId(), channel);
        saveToFile();
        return channel;
    }

    private void loadFromFile(){
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
            data = (Map<UUID, Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }
    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))){
            oos.writeObject(data);
        } catch (IOException e) {  // user파일은 saveTiFile 내용 없는데 어떻게 고쳐야할지
            e.printStackTrace();
        }
    }

}
