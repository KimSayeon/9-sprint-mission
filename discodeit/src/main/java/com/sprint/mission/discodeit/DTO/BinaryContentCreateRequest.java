package com.sprint.mission.discodeit.DTO;

public record BinaryContentCreateRequest (
        byte[] content, //실제 데이터
        String contentType, //파일 형식
        String fileName //파일 이름

){}