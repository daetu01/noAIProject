package com.no.ai.board.controller;

import com.no.ai.board.dto.BoardDTO;
import com.no.ai.board.service.BoardService;
import com.no.ai.global.exception.Message;
import com.no.ai.global.exception.StatusEnum;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.util.List;

@RestController
@RequestMapping("/board")
@AllArgsConstructor
public class BoardController {

    private final BoardService boardService;

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

    @PostMapping
    public ResponseEntity<Message> createBoard(@RequestBody BoardDTO.Post dto) {

        boardService.create(dto);
        Message message = new Message();

        message.setStatus(StatusEnum.OK);
        message.setMessage("성공 코드");

        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
