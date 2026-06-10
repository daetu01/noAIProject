package com.no.ai.music.service;

import com.no.ai.global.security.details.CustomUserDetails;
import com.no.ai.global.service.FileService;
import com.no.ai.music.domain.Music;
import com.no.ai.music.dto.MusicDto;
import com.no.ai.music.repository.MusicRepository;
import com.no.ai.user.domain.UserEntity;
import com.no.ai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MusicService  {
    private final UserRepository userRepository;
    private final MusicRepository musicRepository;
    private final FileService fileService;

    @Transactional
    public void create(MusicDto.CREATE data, MultipartFile audio, MultipartFile cover, CustomUserDetails userDetails) throws IOException {
        UserEntity user = userRepository.findByEmail(userDetails.getUser().getEmail())
                .orElseThrow();

        String audioUrl = fileService.saveAudio(audio);
        String coverImageUrl = fileService.saveImage(cover);


        Music music = Music.builder()
                .artist(data.getArtist())
                .genre(data.getGenre())
                .title(data.getTitle())
                .coverImageUrl(coverImageUrl)
                .audioUrl(audioUrl)
                .description(data.getDescription())
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        musicRepository.save(music);
    }


}
