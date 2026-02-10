package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

// [수정 1] @Service 삭제: 얘는 저장소니까 @Repository가 맞습니다. (Spring 족보 정리)
@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {

    // [수정 2] 경로 통일: 다른 파일들(User, Channel)처럼 'file-data-map' 폴더 안에 저장되도록 수정
    private Path DIRECTORY;
    private Path FILE_PATH;

    private Map<UUID, BinaryContent> data = new HashMap<>();

    // [수정 3] 초기화 로직: 생성자 대신 @PostConstruct 사용 (Spring이 준비되면 실행)
    @PostConstruct
    public void init() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"), "file-data-map", "BinaryContent");
        this.FILE_PATH = DIRECTORY.resolve("binary_contents.dat");

        // 폴더가 없으면 만들기
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException("저장 디렉토리를 생성할 수 없습니다.", e);
            }
        }

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

    @Override
    public List<BinaryContent> findAllByIdIn(Collection<UUID> ids){
        return findAll().stream()
                .filter(content -> ids.contains(content.getId()))
                .collect(Collectors.toList());
    }

    private void saveToFile(){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH.toFile()))){
            oos.writeObject(data);
        } catch (IOException e){
            // [수정 4] 에러 출력 대신 런타임 예외로 던져서 문제 발생 시 바로 알 수 있게 변경
            throw new RuntimeException("파일 저장 중 오류 발생", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFromFile(){
        if (Files.notExists(FILE_PATH)) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH.toFile()))){
            data = (Map<UUID, BinaryContent>) ois.readObject();
        } catch (IOException | ClassNotFoundException e){
            // 파일이 깨졌거나 읽을 수 없으면 빈 맵으로 시작
            data = new HashMap<>();
        }
    }
}