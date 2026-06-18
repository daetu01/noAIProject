package com.no.ai.music.service;

import com.no.ai.board.repository.MusicFavoriteRepository;
import com.no.ai.global.exception.AuthenticationException;
import com.no.ai.global.exception.NotFoundException;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MusicService  {
    private final UserRepository userRepository;
    private final MusicRepository musicRepository;
    private final FileService fileService;
    private final MusicFavoriteRepository musicFavoriteRepository;

    @Transactional
    public void create(MusicDto.CREATE data, MultipartFile audio, MultipartFile cover, CustomUserDetails userDetails) throws IOException {
        UserEntity user = userRepository.findByEmail(userDetails.getUser().getEmail())
                .orElseThrow();


        // 응 음악 나만 올릴거야
        if (!user.getEmail().equals("daiswhat@gmail.com")) {
            throw new AuthenticationException("허용되지 않은 사용자입니다.");
        }

        String audioUrl = fileService.saveAudio(audio);
        String coverImageUrl = fileService.saveImage(cover);


        Music music = Music.builder()
                .artist(data.getArtist())
                .genre(data.getGenre())
                .title(data.getTitle())
                .coverImageUrl(coverImageUrl)
                .audioUrl(audioUrl)
                .description(data.getDescription())
                .play(0L)
                .likedCount(0)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        musicRepository.save(music);
    }

    public List<MusicDto.GET> get(CustomUserDetails customUserDetails) {
        List<Music> musicList = musicRepository.findAll();
        // musicList의 music을 -> MusicDto.GET 으로 변경해야됨

        UserEntity user = null;
        if (customUserDetails != null) {
             user = userRepository.findByEmail(customUserDetails.getUser().getEmail())
                    .orElseThrow(() -> new NotFoundException("선택한 유저를 찾을 수 없습니다."));
        }

        Set<Long> favoriteMusicIds = musicFavoriteRepository.findByUser(user).stream()
                .map(f -> f.getMusic().getId())
                .collect(Collectors.toSet());


        return musicList
                .stream()
                .map(music ->
                        MusicDto.GET.builder()
                                .id(music.getId())
                                .title(music.getTitle())
                                .artist(music.getArtist())
                                .genre(music.getGenre())
                                .audioUrl(music.getAudioUrl())
                                .likedCount(music.getLikedCount())
                                .coverImageUrl(music.getCoverImageUrl())
                                .description(music.getDescription())
                                .liked(favoriteMusicIds.contains(music.getId()))
                                .play(music.getPlay())
                                .nickName(music.getUser().getNickName())
                                .build())
                .toList();
    }

    public MusicDto.GET getDetail(Long id) {
        Music music = musicRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("음악을 찾을 수 없습니다."));

        return MusicDto.GET.builder()
                .id(music.getId())
                .title(music.getTitle())
                .artist(music.getArtist())
                .genre(music.getGenre())
                .audioUrl(music.getAudioUrl())
                .coverImageUrl(music.getCoverImageUrl())
                .likedCount(music.getLikedCount())
                .play(music.getPlay())
                .description(music.getDescription())
                .build();
    }

    @Transactional
    public void update(MusicDto.PUT dto, CustomUserDetails customUserDetails) {
        Music music = musicRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("찾을 수 없는 곡입니다."));

        if (!music.getUser().getEmail().equals(customUserDetails.getUser().getEmail()))
        {
            throw new AuthenticationException("수정 권한이 없습니다.");
        }

        music.of(
                dto.getTitle(),
                dto.getArtist(),
                dto.getDescription(),
                dto.getGenre(),
                dto.getAudioUrl(),
                dto.getCoverImageUrl()
        );
    }

    @Transactional
    public void delete(Long id, CustomUserDetails customUserDetails) {
        Music music = musicRepository.findById(id).orElseThrow(
                () -> new NotFoundException("음악을 찾을 수 없습니다."));

        if (!music.getUser().getEmail().equals(customUserDetails.getUser().getEmail()))
        {
            throw new AuthenticationException("삭제 권한이 없습니다.");
        }

        musicRepository.delete(music);
    }


    @Transactional
    public void increasePlay(Long id) {
        Music music = musicRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("음악을 찾을 수 없습니다."));

        music.increasePlay();
    }

}
