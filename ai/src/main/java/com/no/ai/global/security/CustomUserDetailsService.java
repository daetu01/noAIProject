package com.no.ai.global.security;

import com.no.ai.global.security.details.CustomUserDetails;
import com.no.ai.global.security.dto.CustomUserInfoDto;
import com.no.ai.user.domain.UserEntity;
import com.no.ai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findById(Long.parseLong(username))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        CustomUserInfoDto customUserInfoDto = CustomUserInfoDto.from(user);

        return new CustomUserDetails(customUserInfoDto);
    }
}
