package com.no.ai.board.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.core.io.Resource;

public class ImageDto {

    @Builder
    @Getter
    public static class Response {
        private Resource resource;
        private String contentType;
    }
}
