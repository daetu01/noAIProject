package com.no.ai.board.controller;

import com.no.ai.board.domain.Board;
import com.no.ai.board.dto.BoardDTO;
import com.no.ai.board.dto.ImageDto;
import com.no.ai.board.service.BoardService;
import com.no.ai.global.service.FileService;
import com.no.ai.global.exception.Message;
import com.no.ai.global.exception.StatusEnum;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.Charset;
import java.util.List;

@RestController
@RequestMapping("/board")
@AllArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final FileService fileService;

    // 게시글 조회
    @GetMapping
    public ResponseEntity<Message> readBoard() {
        Message message = new Message();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        List<BoardDTO.Get> boardList = boardService.read();
        message.setStatus(StatusEnum.OK);
        message.setMessage("성공 코드");
        message.setData(boardList);
        return new ResponseEntity<>(message, headers, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Message> createBoard(@RequestPart("dto") BoardDTO.Post dto,
                                               @RequestPart("file") MultipartFile file) {


        Board board = boardService.create(dto);
        fileService.fileUpload(board, file);
        Message message = new Message();

        message.setStatus(StatusEnum.OK);
        message.setMessage("성공 코드");

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Message> updateBoard(@RequestBody BoardDTO.Put dto) {
        boardService.update(dto);

        Message message = new Message();
        message.setStatus(StatusEnum.OK);
        message.setMessage("성공 코드");

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Message> deleteBoard(@RequestParam Long id) {
        boardService.delete(id);

        Message message = new Message();
        message.setStatus(StatusEnum.OK);
        message.setMessage("성공 코드");

        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Message> getBoard(@PathVariable Long id) {
        BoardDTO.Get board = boardService.getBoard(id);

        Message message = new Message();
        message.setStatus(StatusEnum.OK);
        message.setMessage("성공 코드");
        message.setData(board);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<Resource> getImage(@PathVariable Long id) {
        ImageDto.Response response = boardService.getImage(id);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.getContentType()))
                .body(response.getResource());
    }

}
