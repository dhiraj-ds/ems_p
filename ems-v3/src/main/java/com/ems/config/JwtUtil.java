package com.ems.config;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT Utility — access token (30-min) generation and validation.
 *
 * Refresh flow requires distinguishing between:
 *   - Invalid/tampered token  → reject outright
 *   - Expired but valid sig   → allow refresh token to take over
 *
 * Both isValid() and isExpiredButOtherwiseValid() are used by JwtAuthFilter.
 */
@Component @Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")        private String secret;
    @Value("${jwt.expiration-ms}") private long   expirationMs;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    /** Generate a signed access JWT. Expires in 30 minutes. */
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getKey())
                .compact();
    }

    /** Extract username (subject). Throws JwtException if token is invalid. */
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /** Extract role claim. */
    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    /**
     * Returns true if the token has a valid signature AND is not yet expired.
     * Used for normal per-request authentication.
     */
    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("JWT not valid: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Returns true if the token has a valid signature but IS expired.
     * Used by JwtAuthFilter to decide whether to attempt a refresh:
     *   - expired + valid sig  → try refresh token
     *   - tampered/garbage     → reject immediately (returns false)
     */
    public boolean isExpiredButOtherwiseValid(String token) {
        try {
            // Parse without expiry validation to check signature only
            Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token);
            return false; // signature valid AND not expired → isValid() would return true
        } catch (ExpiredJwtException e) {
            return true;  // ← exactly what we want: expired but signature was fine
        } catch (JwtException | IllegalArgumentException e) {
            return false; // tampered or garbage — not safe to refresh
        }
    }

    /**
     * Extract claims from an expired token (needed to get username for refresh).
     * Only called after isExpiredButOtherwiseValid() confirms the signature.
     */
    public Claims extractExpiredClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims(); // JJWT exposes claims even on expiry
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}