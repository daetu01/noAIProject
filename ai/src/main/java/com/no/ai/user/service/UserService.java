package com.no.ai.user.service;

import com.no.ai.global.security.dto.CustomUserInfoDto;
import com.no.ai.global.security.jwt.JwtUtil;
import com.no.ai.user.domain.UserEntity;
import com.no.ai.user.dto.LoginRequestDto;
import com.no.ai.user.dto.UserDTO;
import com.no.ai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ModelMapper mapper;

    public Long create(UserEntity entity) {

        // 유저 저장하기
        return userRepository.save(entity).getId();
    }

    public String login(LoginRequestDto userDto) {
        // login
        UserEntity user = userRepository.findByEmail(userDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 잘못됐습니다."));

        // 2. 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 잘못되었습니다.");
        }

        CustomUserInfoDto loginDto = mapper.map(user, CustomUserInfoDto.class);

        // 3. 인증 성공 시 JWT 토큰 생성 및 반환
        return jwtUtil.createAccessToken(loginDto);
    }
}
