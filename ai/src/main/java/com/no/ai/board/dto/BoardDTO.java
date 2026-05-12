package com.no.ai.board.dto;

import lombok.*;

public class BoardDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Post {
        String title;
        String content;
        String writer;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Get {
        Long id;
        String title;
        String content;
        String writer;
        String uploadDir;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Put {
        Long id;
        String title;
        String content;
        String writer;
    }
}
