package com.no.ai.board.service;

import com.no.ai.board.domain.Board;
import com.no.ai.board.domain.Comment;
import com.no.ai.board.dto.CommentDto;
import com.no.ai.board.repository.BoardRepository;
import com.no.ai.board.repository.CommentRepository;
import com.no.ai.global.security.details.CustomUserDetails;
import com.no.ai.user.domain.UserEntity;
import com.no.ai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    public void create(CommentDto.CREATE dto, CustomUserDetails userDetails) {
        UserEntity user = userRepository.findByEmail(userDetails.getUser().getEmail())
                .orElseThrow();

        Board board = boardRepository.findById(dto.getBoardId())
                .orElseThrow();

        Comment comment = Comment.builder()
                .content(dto.getContent())
                .board(board)
                .user(user)
                .build();

        commentRepository.save(comment);
    }

    public List<CommentDto.GET> getByBoardId(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow();
        return commentRepository.findByBoard(board).stream()
                .map(c -> CommentDto.GET.builder()
                        .id(c.getId())
                        .nickName(c.getUser().getNickName())
                        .content(c.getContent())
                        .build())
                .toList();
    }
}
