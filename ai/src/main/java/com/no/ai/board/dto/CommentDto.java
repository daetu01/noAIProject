package com.no.ai.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommentDto {

    @Getter
    public static class CREATE {
        private Long boardId;
        private String content;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GET {
        private Long id;
        private String nickName;
        private String content;
    }

    @Getter
    public static class PUT {
        private String content;
    }
}
