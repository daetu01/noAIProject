package com.no.ai.music.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MusicDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CREATE {
        private String title;
        private String artist;
        private String description;
        private String genre;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GET {
        private Long id;
        private String title;
        private String artist;
        private String description;
        private String genre;
        private String audioUrl;
        private String coverImageUrl;
        private Long play;
        private int likedCount;
        private boolean liked;
        private String nickName;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PUT {
        private Long id;
        private String title;
        private String artist;
        private String description;
        private String genre;
        private String audioUrl;
        private String coverImageUrl;
    }
}
