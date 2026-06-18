package com.no.ai.board.service;

import com.no.ai.board.domain.Board;
import com.no.ai.board.dto.BoardDTO;
import com.no.ai.board.dto.ImageDto;
import com.no.ai.board.repository.BoardFavoriteRepository;
import com.no.ai.board.repository.BoardRepository;
import com.no.ai.global.exception.AuthenticationException;
import com.no.ai.global.exception.NotFoundException;
import com.no.ai.global.security.details.CustomUserDetails;
import com.no.ai.user.domain.UserEntity;
import com.no.ai.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardFavoriteRepository boardFavoriteRepository;
    private final ModelMapper modelMapper;

    public List<BoardDTO.Get> read(CustomUserDetails userDetails) {
        List<Board> boardList = boardRepository.findAll();

        UserEntity user = userRepository.findById(userDetails.getUser().getUserId())
                .orElseThrow();
        Set<Long> favoriteBoardIds = boardFavoriteRepository.findByUser(user).stream()
                .map(f -> f.getBoard().getId())
                .collect(Collectors.toSet());


        // 리스트 순회하면서 favorite 리스트에 해당되는 Board가 있는 지 찾기?
        // id가 좋아요한 Board들을 갖고 오기.
        // 해당하는 Board들의 isFavorite 값들을 True로 변경해주기?
        return boardList.stream()
                .map(board -> BoardDTO.Get.builder()
                        .id(board.getId())
                        .title(board.getTitle())
                        .content(board.getContent())
                        .writer(board.getWriter())
                        .uploadDir(board.getUploadDir())
                        .liked(favoriteBoardIds.contains(board.getId()))
                        .likedCount(board.getLikedCount())
                        .build())
                .toList();
    }

    @Transactional
    public Board create(BoardDTO.Post dto, CustomUserDetails userDetails) {
        Board board = Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(userDetails.getUser().getNickName())
                .likedCount(0)
                .build();

        return boardRepository.save(board);
    }

    @Transactional
    public void update(BoardDTO.Put dto, CustomUserDetails userDetails) {
        Board board = boardRepository.findById(dto.getId())
                        .orElseThrow(() -> new NotFoundException("해당 게시글이 없습니다."));

        if (!dto.getWriter().equals(userDetails.getUser().getNickName())) {
            throw new AuthenticationException("게시글 작성자가 아닌 사용자입니다.");
        }

        board.update(dto.getTitle(), dto.getContent());
    }

    @Transactional
    public void delete(Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Board board = boardRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("해당 게시글이 없습니다."));

        // User 검증 코드.
        if (!board.getWriter().equals(userDetails.getUser().getNickName())) {
            throw new AuthenticationException("게시글 작성자가 아닌 사용자입니다.");
        }

        boardRepository.delete(board);
    }

    public BoardDTO.Get getBoard(Long id, CustomUserDetails userDetails) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다."));

        BoardDTO.Get dto =  modelMapper.map(board, BoardDTO.Get.class);

        UserEntity user = userRepository.findById(userDetails.getUser().getUserId())
                .orElseThrow();

        Set<Long> favoriteBoardIds = boardFavoriteRepository.findByUser(user).stream()
                .map(f -> f.getBoard().getId())
                .collect(Collectors.toSet());

        if (favoriteBoardIds.contains(id)) {
            dto.setLiked(true);
        }

        return dto;
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
