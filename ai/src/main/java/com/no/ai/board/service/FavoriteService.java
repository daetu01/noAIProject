package com.no.ai.board.service;

import com.no.ai.board.domain.Board;
import com.no.ai.board.domain.BoardFavorite;
import com.no.ai.board.domain.MusicFavorite;
import com.no.ai.board.repository.BoardFavoriteRepository;
import com.no.ai.board.repository.BoardRepository;
import com.no.ai.board.repository.MusicFavoriteRepository;
import com.no.ai.global.exception.NotFoundException;
import com.no.ai.music.domain.Music;
import com.no.ai.music.repository.MusicRepository;
import com.no.ai.user.domain.UserEntity;
import com.no.ai.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final BoardFavoriteRepository boardFavoriteRepository;
    private final MusicFavoriteRepository musicFavoriteRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final MusicRepository musicRepository;

    @Transactional
    public void boardFavorite(Long id, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow();

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("찾을 수 없는 게시글입니다."));

        // 추후 redis로 변경하는 게 좋을 듯

        // 만약 존재하지 않는다면 좋아요 ON
        if (!boardFavoriteRepository.existsByUserAndBoard(user, board)){
            BoardFavorite favorite = BoardFavorite.builder()
                    .board(board)
                    .user(user)
                    .build();

            boardFavoriteRepository.save(favorite);
            board.setLikedCount(board.getLikedCount() + 1);

        } else {
            // 존재한다면 좋아요 취소
            BoardFavorite favorite = boardFavoriteRepository.findByUserAndBoard(user,board);
            boardFavoriteRepository.delete(favorite);
            board.setLikedCount(board.getLikedCount() - 1);
        }
    }

    @Transactional
    public void musicFavorite(Long id, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow();

        Music music = musicRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("찾을 수 없는 음악입니다."));

        // 만약 존재하지 않는다면 좋아요 ON
        if (!musicFavoriteRepository.existsByUserAndMusic(user, music)){
            MusicFavorite favorite = MusicFavorite.builder()
                    .music(music)
                    .user(user)
                    .build();

            musicFavoriteRepository.save(favorite);
            music.increaseLiked();

        } else {
            // 존재한다면 좋아요 취소
            MusicFavorite favorite = musicFavoriteRepository.findByUserAndMusic(user,music);
            musicFavoriteRepository.delete(favorite);
            music.decreaseLiked();
        }
    }
}
