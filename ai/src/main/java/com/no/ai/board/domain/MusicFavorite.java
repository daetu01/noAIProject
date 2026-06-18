package com.no.ai.board.domain;


import com.no.ai.music.domain.Music;
import com.no.ai.user.domain.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "user_id",
                                "music_id"
                        }
                )
        }
)
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MusicFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "music_id")
    private Music music;
}
