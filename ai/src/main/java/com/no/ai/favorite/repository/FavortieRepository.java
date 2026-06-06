package com.no.ai.favorite.repository;

import com.no.ai.board.domain.Board;
import com.no.ai.favorite.domain.Favorite;
import com.no.ai.user.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavortieRepository extends JpaRepository<Favorite, Long> {
    boolean existsByUserAndBoard(
            UserEntity user,
            Board board
    );

    Favorite findByUserAndBoard(
            UserEntity user,
            Board board
    );
}
