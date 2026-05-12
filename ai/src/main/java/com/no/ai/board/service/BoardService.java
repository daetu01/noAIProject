package com.no.ai.board.service;

import com.no.ai.board.domain.Board;
import com.no.ai.board.dto.BoardDTO;
import com.no.ai.board.dto.ImageDto;
import com.no.ai.board.repository.BoardRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@AllArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final ModelMapper modelMapper;

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

    public BoardDTO.Get getBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        return modelMapper.map(board, BoardDTO.Get.class);
    }

    public ImageDto.Response getImage(Long id) {
        try {
            // DB에서 해당 게시글의 uploadDir 조회
            String filePath = boardRepository.findById(id).get().getUploadDir();
            Path path = Paths.get(filePath);
            Resource resource = new FileSystemResource(path);

            String contentType = Files.probeContentType(path); // "image/jpeg" 등

            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ImageDto.Response.builder()
                    .resource(resource)
                    .contentType(contentType)
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* 추후 예외처리 해줘야됨 */
        return null;
    }
}
