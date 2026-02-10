package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.DTO.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.DTO.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // [수정 1] 롬복 사용: 생성자 코드 자동 생성 (코드가 깔끔해짐)
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;

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
        // [수정 2] 성능 개선: 전체를 다 불러와서 메모리에서 거르지 않고,
        // Repository에게 "이 ID들만 찾아줘"라고 요청 (Repository에 구현된 기능 활용)
        List<BinaryContent> contents = binaryContentRepository.findAllByIdIn(ids);

        return contents.stream()
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