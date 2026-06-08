package com.no.ai.board.service;

import com.no.ai.board.domain.Board;
import com.no.ai.board.repository.BoardRepository;
import com.no.ai.board.domain.Favorite;
import com.no.ai.board.repository.FavoriteRepository;
import com.no.ai.user.domain.UserEntity;
import com.no.ai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favortieRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    public void favorite(Long id, String email) {
        System.out.println("Email: " + email);
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow();

        Board board = boardRepository.getReferenceById(id);

        // 추후 redis로 변경하는 게 좋을 듯

        // 만약 존재하지 않는다면 좋아요 ON
        if (!favortieRepository.existsByUserAndBoard(user, board)){
            Favorite favorite = Favorite.builder()
                    .board(board)
                    .user(user)
                    .build();

            favortieRepository.save(favorite);
            board.setLikedCount(board.getLikedCount() + 1);
            boardRepository.save(board);
        } else {
            // 존재한다면 좋아요 취소
            Favorite favorite = favortieRepository.findByUserAndBoard(user,board);
            favortieRepository.delete(favorite);
            board.setLikedCount(board.getLikedCount() - 1);
            boardRepository.save(board);
        }
    }
}
