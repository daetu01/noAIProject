package com.no.ai.user.controller;

import com.no.ai.global.exception.Message;
import com.no.ai.user.domain.UserEntity;
import com.no.ai.user.dto.LoginRequestDto;
import com.no.ai.user.dto.UserDTO;
import com.no.ai.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    @PostMapping("/login")
    public ResponseEntity<String> getUserProfile(
            @RequestBody LoginRequestDto request
    ){
        String token = userService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @PostMapping("/signup")
    public ResponseEntity<Long> createUser(@RequestBody UserDTO.CREATE dto) {
        System.out.println(dto.getEmail() + " " + dto.getNickName() + " " + dto.getPassword());
        UserEntity entity = modelMapper.map(dto, UserEntity.class);
        Long id = userService.create(entity);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

}
