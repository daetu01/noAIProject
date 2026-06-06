package com.no.ai.favorite.controller;

import com.no.ai.favorite.service.FavoriteService;
import com.no.ai.global.security.details.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/favorite")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @PostMapping("/{id}")
    public void favorite(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        favoriteService.favorite(id, userDetails.getUsername());
    }
}
