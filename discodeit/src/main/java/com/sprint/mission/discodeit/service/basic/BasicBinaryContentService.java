package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.DTO.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;



    public BasicBinaryContentService(BinaryContentRepository binaryContentRepository){
        this.binaryContentRepository = binaryContentRepository;
    }

    @Override
    public BinaryContentResponse create(BinaryContentCreateRequest request){
        BinaryContent binaryContent = new BinaryContent(
                request.content(),
                request.contentType(),
                request.fileName()
        );
        BinaryContent saved = binaryContentRepository.save(binaryContent);
        return convertToResponse(saved);
    }

    @Override
    public BinaryContentResponse findById(UUID id){
        BinaryContent binaryContent = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("파일을 찾을 수 없습니다. ID: " + id));
            return convertToResponse(binaryContent);
    }

    @Override
    public List<BinaryContentResponse> findAll(List<UUID> ids){
        List<BinaryContent> allContents = binaryContentRepository.findAll();

        return allContents.stream()
                .filter(content -> ids.contains(content.getId()))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        binaryContentRepository.deleteById(id);
    }

    private BinaryContentResponse convertToResponse(BinaryContent entity){
        return new BinaryContentResponse(
                entity.getId(),
                entity.getContent(),
                entity.getContentType(),
                entity.getFileName(),
                entity.getCreatedAt()
        );
    }


}
