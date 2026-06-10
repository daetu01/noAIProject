package com.no.ai.board.controller;

import com.no.ai.board.service.FavoriteService;
import com.no.ai.global.security.details.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @PostMapping("/{id}")
    public void favorite(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        favoriteService.favorite(id, userDetails.getUser().getEmail());
    }
}
