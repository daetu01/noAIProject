package com.no.ai.board.repository;

import com.no.ai.board.domain.Board;
import com.no.ai.board.domain.BoardFavorite;
import com.no.ai.user.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardFavoriteRepository extends JpaRepository<BoardFavorite, Long> {
    boolean existsByUserAndBoard(
            UserEntity user,
            Board board
    );

    BoardFavorite findByUserAndBoard(
            UserEntity user,
            Board board
    );

    List<BoardFavorite> findByUser(
            UserEntity user
    );

    int countByBoard(Board board);
}
