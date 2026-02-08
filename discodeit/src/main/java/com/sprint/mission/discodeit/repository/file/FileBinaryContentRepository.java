package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {
    private static final String FILE_PATH = "binary_contents.dat";
    private Map<UUID, BinaryContent> data = new HashMap<>();

    public FileBinaryContentRepository(){
        loadFromFile();
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent){
        data.put(binaryContent.getId(), binaryContent);
        saveToFile();
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID id){
        if (data.containsKey(id)) {
            data.remove(id);
            saveToFile();
        }
    }

    private void saveToFile(){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))){
            oos.writeObject(data);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void loadFromFile(){
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))){
            data = (Map<UUID, BinaryContent>) ois.readObject();
        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
            data = new HashMap<>();
        }
    }
    @Override
    public List<BinaryContent> findAllByIdIn(Collection<UUID> ids){
        List<BinaryContent> allContents = findAll();

        return allContents.stream()
                .filter(content -> ids.contains(content.getId()))
                .collect(Collectors.toList());
    }


}
