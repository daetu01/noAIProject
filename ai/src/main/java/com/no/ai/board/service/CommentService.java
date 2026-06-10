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
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ModelMapper mapper;

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

    public void delete(Long commentId, CustomUserDetails userDetails) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow();

        if (!comment.getUser().getEmail().equals(userDetails.getUser().getEmail())) {
            return ;
        }
        commentRepository.delete(comment);
    }

    public CommentDto.GET put(Long commentId, CommentDto.PUT dto, CustomUserDetails userDetails) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow();

        if (!comment.getUser().getEmail().equals(userDetails.getUser().getEmail())) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        comment.setContent(dto.getContent());

        commentRepository.save(comment);

        return CommentDto.GET.builder()
                .content(comment.getContent())
                .build();
    }
}
