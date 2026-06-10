package com.no.ai.music.dto;

import lombok.Getter;

public class MusicDto {

    @Getter
    public static class CREATE {
        private String title;
        private String artist;
        private String description;
        private String genre;
    }
}
