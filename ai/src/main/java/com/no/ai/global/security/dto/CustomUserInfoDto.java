package com.no.ai.global.security.dto;

import com.no.ai.user.domain.UserEntity;
import com.no.ai.user.domain.UserRole;
import com.no.ai.user.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomUserInfoDto extends UserDTO {
    private Long userId;
    private String nickName;
    private String password;
    private String email;
    private UserRole userRole;

    public static CustomUserInfoDto from(UserEntity user) {
        return CustomUserInfoDto.builder()
                .userId(user.getId())
                .userRole(user.getUserRole())
                .email(user.getEmail())
                .nickName(user.getNickName())
                .build();
    }
}
