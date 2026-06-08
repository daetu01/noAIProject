package com.no.ai.board.controller;

import com.no.ai.board.dto.CommentDto;
import com.no.ai.board.service.CommentService;
import com.no.ai.global.security.details.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public void create(@RequestBody CommentDto.CREATE dto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.create(dto, userDetails);
    }

    @GetMapping("/{boardId}")
    public List<CommentDto.GET> getComments(@PathVariable Long boardId) {
        return commentService.getByBoardId(boardId);
    }
}
