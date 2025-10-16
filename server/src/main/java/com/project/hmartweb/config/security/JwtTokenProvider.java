package com.project.hmartweb.config.security;

import com.project.hmartweb.domain.entities.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.secret-key}")
    private String secretKey;
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    public String generateToken(User user) {
        Map<String, Object> claims = Map.of(
                "id", user.getId(),
                "role", user.getRole().getId().toString()
        );
        return Jwts.builder()
                .claims(claims)
                .subject(user.getUserName())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                .signWith(SignatureAlgorithm.HS256, getSecretKey())
                .compact();

    }

    private Key getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Timestamp getExpirationDate(String token) {
        return new Timestamp(extractClaim(token, Claims::getExpiration).getTime());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenExpired(String token) {
        return getExpirationDate(token).before(new Date());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        try {
            if (username.equals(userDetails.getUsername()) && !isTokenExpired(token)) {
                return true;
            }
            Jwts.parser().setSigningKey(getSecretKey()).build().parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("WT claims string is empty.");
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature.");
        }
        return false;
    }
}
