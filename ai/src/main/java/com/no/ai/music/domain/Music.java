package com.no.ai.music.domain;

import com.no.ai.user.domain.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Music {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String artist;

    private String description;

    private String genre;

    private String audioUrl;

    private String coverImageUrl;

    private Long play;

    private int likedCount;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    public void of(String title, String artist, String description, String genre, String audioUrl, String coverImageUrl) {
        this.title = title;
        this.artist = artist;
        this.description = description;
        this.genre = genre;
        this.audioUrl = audioUrl;
        this.coverImageUrl = coverImageUrl;
    }

    public void increasePlay() {
        this.play += 1;
    }
    public void increaseLiked() {
        this.likedCount++ ;
    }
    public void decreaseLiked() {
        this.likedCount-- ;
    }
}
