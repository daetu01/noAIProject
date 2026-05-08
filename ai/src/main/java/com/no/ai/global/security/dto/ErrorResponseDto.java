package com.no.ai.global.security.dto;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
public class ErrorResponseDto {
    private int errorCode;
    private String message;
    private LocalDateTime localDateTime;
}
