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

    @PostMapping("/board/{id}")
    public void boardFavorite(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        favoriteService.boardFavorite(id, userDetails.getUser().getEmail());
    }

    @PostMapping("/music/{id}")
    public void musicFavorite(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        favoriteService.musicFavorite(id, userDetails.getUser().getEmail());
    }
}
