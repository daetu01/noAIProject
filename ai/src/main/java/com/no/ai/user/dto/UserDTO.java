package com.no.ai.user.dto;

import lombok.Getter;

public class UserDTO {

    @Getter
    public static class CREATE {
        private String nickName;
        private String email;
        private String password;
    }
}
