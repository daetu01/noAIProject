package com.no.ai.board.service;

import com.no.ai.board.domain.Board;
import com.no.ai.board.dto.BoardDTO;
import com.no.ai.board.repository.BoardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    public List<BoardDTO.Get> read() {
        List<Board> boardList = boardRepository.findAll();

        return boardList.stream()
                .map(board -> BoardDTO.Get.builder()
                        .id(board.getId())
                        .title(board.getTitle())
                        .content(board.getContent())
                        .writer(board.getWriter())
                        .build())
                .toList();
    }

    public Board create(BoardDTO.Post dto) {
        Board board = Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(dto.getWriter())
                .build();

        return boardRepository.save(board);
    }

    public void update(BoardDTO.Put dto) {
        Board board = boardRepository.findById(dto.getId())
                        .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));

        board.update(dto.getTitle(), dto.getContent());

        boardRepository.save(board);
    }

    public void delete(Long id) {
        Board board = boardRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다."));
        boardRepository.delete(board);
    }
}
