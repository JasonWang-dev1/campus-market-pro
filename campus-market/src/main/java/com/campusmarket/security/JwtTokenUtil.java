package com.campusmarket.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT 令牌工具类
 */
@Component
public class JwtTokenUtil {

    private final SecretKey signingKey;
    private final long expiration;

    public JwtTokenUtil(@Value("${jwt.secret}") String secret,
                        @Value("${jwt.expiration}") long expiration) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expiration = expiration;
    }

    /**
     * 生成令牌
     */
    public String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration * 1000);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    /**
     * 从令牌中获取 Claims
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 获取用户 ID
     */
    public Long getUserIdFromToken(String token) {
        return Long.valueOf(getClaimsFromToken(token).getSubject());
    }

    /**
     * 验证令牌
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 获取令牌过期时间
     */
    public Date getExpirationFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    /**
     * 检查令牌是否已过期
     */
    public boolean isTokenExpired(String token) {
        return getExpirationFromToken(token).before(new Date());
    }
}
