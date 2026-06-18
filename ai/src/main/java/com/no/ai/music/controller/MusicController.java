package com.no.ai.music.controller;

import com.no.ai.global.security.details.CustomUserDetails;
import com.no.ai.music.dto.MusicDto;
import com.no.ai.music.service.MusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/music")
@RequiredArgsConstructor
public class MusicController {
    private final MusicService musicService;

    @PostMapping
    public void uploadMusic (
            @RequestPart("data") MusicDto.CREATE data,
            @RequestPart("audio")MultipartFile audio,
            @RequestPart(value = "cover", required = false) MultipartFile cover,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) throws IOException {

        musicService.create(data, audio, cover, userDetails);
    }

    @GetMapping
    public List<MusicDto.GET> loadMusic(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return musicService.get(customUserDetails);
    }

    @GetMapping("/{id}")
    public MusicDto.GET detail(
            @PathVariable Long id
    ) {
        return musicService.getDetail(id);
    }

    @PutMapping("/{id}")
    public void update(
            @RequestBody MusicDto.PUT dto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ){
        musicService.update(dto, customUserDetails);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        musicService.delete(id, customUserDetails);
    }

    @PostMapping("/{id}/play")
    public void increasePlay(Long id) {
        musicService.increasePlay(id);
    }

}
