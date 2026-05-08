package com.no.ai.global.security;

import com.no.ai.global.security.dto.ErrorResponseDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@AllArgsConstructor
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("인증되지 않은 사용자가 접근했습니다. {}", request.getRequestURI());


        // 응답 설정 (JSON 타입, 401 상태코드)
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);


        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpServletResponse.SC_NON_AUTHORITATIVE_INFORMATION,
                "인증되지 않은 사용자입니다.",
                LocalDateTime.now()
        );

        String result = objectMapper.writeValueAsString(errorResponseDto);
        response.getWriter().write(result);
    }
}
