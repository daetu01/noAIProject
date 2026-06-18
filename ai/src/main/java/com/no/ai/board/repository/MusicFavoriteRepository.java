package com.no.ai.board.repository;

import com.no.ai.board.domain.Board;
import com.no.ai.board.domain.BoardFavorite;
import com.no.ai.board.domain.MusicFavorite;
import com.no.ai.music.domain.Music;
import com.no.ai.user.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusicFavoriteRepository extends JpaRepository<MusicFavorite, Long> {

    boolean existsByUserAndMusic(
            UserEntity user,
            Music music
    );

    MusicFavorite findByUserAndMusic(
            UserEntity user,
            Music music
    );

    List<MusicFavorite> findByUser(
            UserEntity user
    );
}
