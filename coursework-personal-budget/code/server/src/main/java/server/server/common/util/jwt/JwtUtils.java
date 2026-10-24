package server.server.common.util.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import server.server.common.entity.auth.Account;
import server.server.common.util.log.AppLogger;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtils {

    private static final String LOG = AppLogger.name(JwtUtils.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Account account) {
        return Jwts.builder()
                .setSubject(account.getUsername())
                .claim("role", account.getRole())
                .claim("id", account.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /** Generates a cryptographically random opaque refresh token (UUID-based). */
    public String generateRefreshToken() {
        return UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
    }

    public boolean validateAccessToken(String jwt) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(jwt);
            return true;
        } catch (ExpiredJwtException e) {
            AppLogger.warn("JWT expired: " + e.getMessage(), LOG);
        } catch (UnsupportedJwtException e) {
            AppLogger.error("Unsupported JWT: " + e.getMessage(), LOG);
        } catch (MalformedJwtException e) {
            AppLogger.error("Malformed JWT: " + e.getMessage(), LOG);
        } catch (SecurityException e) {
            AppLogger.error("Invalid JWT signature: " + e.getMessage(), LOG);
        } catch (IllegalArgumentException e) {
            AppLogger.error("JWT claims string is empty: " + e.getMessage(), LOG);
        }
        return false;
    }

    public String getUsername(String jwt) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
    }
}
