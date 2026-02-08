package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.util.*;
import java.util.stream.Collectors;

public class JCFBinaryContentRepository implements BinaryContentRepository {
    private final Map<UUID, BinaryContent> repository = new HashMap<>();

    @Override
    public BinaryContent save(BinaryContent binaryContent){
        repository.put(binaryContent.getId(), binaryContent);
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id){
        return Optional.ofNullable(repository.get(id));
    }

    @Override
    public List<BinaryContent> findAll(){
        return new ArrayList<>(repository.values());
    }

    @Override
    public void deleteById(UUID id){
        repository.remove(id);
    }

    @Override
    public List<BinaryContent> findAllByIdIn(Collection<UUID> ids){
        return repository.values().stream()
                .filter(content -> ids.contains(content.getId()))
                .collect(Collectors.toList());
    }
}
