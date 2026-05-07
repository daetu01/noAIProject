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
                        .title(board.getTitle())
                        .content(board.getContent())
                        .writer(board.getWriter())
                        .build())
                .toList();
    }

    public void create(BoardDTO.Post dto) {
        Board board = Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(dto.getWriter())
                .build();

        boardRepository.save(board);
    }

}
