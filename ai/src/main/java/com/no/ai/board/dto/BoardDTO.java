package com.no.ai.board.dto;

import com.no.ai.board.domain.Comment;
import lombok.*;

import java.util.List;

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
        boolean liked;
        int likedCount;
        List<CommentDto.GET> comments;
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
