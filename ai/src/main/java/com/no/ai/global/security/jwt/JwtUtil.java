package com.no.ai.global.security.jwt;


import com.no.ai.global.security.dto.CustomUserInfoDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;


@Slf4j
@Component

public class JwtUtil {

    private final Key key;
    private final long accessTokenExpTime;

    public JwtUtil(
            @Value("${jwt.secret}") final String secretKey,
            @Value("${jwt.expiration_time}") final long accessTokenExpTime
    )
    {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpTime = accessTokenExpTime;
    }

    /**
     *
     * @param user
     * @return
     */
    public String createAccessToken(CustomUserInfoDto user) {
        return createToken(user, this.accessTokenExpTime);
    }

    public String createToken(CustomUserInfoDto user, long duration) {
        Claims claims = Jwts.claims();
        claims.put("userId", user.getUserId());
        claims.put("email", user.getEmail());
        claims.put("nickName", user.getNickName());
        claims.put("role", user.getUserRole());

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(duration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().    parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty", e);
        }
        return false;
    }

    /**
     * Token에서 User ID 추출
     *
     * @param token
     * @return User ID
     */
    public Long getUserId(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    /**
     * JWT Claims 추출
     * @param accessToken
     * @return JWT claims
     */
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
