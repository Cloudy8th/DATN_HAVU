package com.project.hmartweb.config.security;

import com.project.hmartweb.domain.entities.User;
import io.jsonwebtoken.*;
// import io.jsonwebtoken.io.Decoders; // REMOVE: Not used in 0.11.x
// import io.jsonwebtoken.security.Keys; // REMOVE: Not used in 0.11.x
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

// import java.security.Key; // REMOVE: Key is not strictly needed for 0.11.x signing
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    // You are still using this import: import io.jsonwebtoken.security.Keys;
    // but the actual signing key in 0.11.x is just the String secretKey or a byte[]
    // The previous error about 'log' existing suggests you might have had @Slf4j before.
    // I will keep your manual logger declaration.
    
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
                // FIX 1: Replace .claims() with .setClaims() (0.11.x method)
                .setClaims(claims) 
                // FIX 2: Replace .subject() with .setSubject()
                .setSubject(user.getUserName()) 
                // FIX 3: Replace .issuedAt() with .setIssuedAt()
                .setIssuedAt(new Date(System.currentTimeMillis())) 
                // FIX 4: Replace .expiration() with .setExpiration()
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L)) 
                // FIX 5: Use the String secret key for signing (0.11.x method)
                .signWith(SignatureAlgorithm.HS256, secretKey) 
                .compact();
    }

    /*
    // REMOVE: This method is not needed and uses the new API (0.12.x+)
    private Key getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    */

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                // FIX 6: Remove the .build() call (not present in 0.11.x JwtParser)
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
            // FIX 7: Simplify validation logic. The check is redundant if parseClaimsJws is called.
            // Also, replace getSecretKey() (which we removed) with the secretKey String.
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token); 
            
            // Check username *after* successful parsing/validation
            if (!username.equals(userDetails.getUsername())) {
                return false;
            }
            return true;
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty: {}", ex.getMessage());
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature: {}", ex.getMessage());
        }
        return false;
    }
}