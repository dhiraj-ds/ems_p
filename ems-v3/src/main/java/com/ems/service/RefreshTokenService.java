package com.ems.service;

import com.ems.entity.RefreshToken;
import com.ems.entity.User;
import com.ems.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * RefreshTokenService — all refresh token lifecycle operations.
 *
 * Rotation policy (security best practice):
 *   On every successful refresh → delete the old token row → create a new UUID.
 *   This means each refresh token can only be used ONCE.
 *   If an attacker steals the refresh cookie and uses it first, the legitimate
 *   user's next request will fail (token not found) → forced re-login.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository repo;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs; // 604_800_000 ms = 7 days

    // ── Create ────────────────────────────────────────────────────────────────

    /**
     * Issue a new refresh token for a user.
     * Also purges any expired tokens from the table to keep it clean.
     */
    public RefreshToken createToken(User user) {
        // Clean house first — remove any stale expired rows
        repo.deleteAllExpired(Instant.now());

        RefreshToken token = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plusMillis(refreshExpirationMs))
                .createdAt(Instant.now())
                .build();

        RefreshToken saved = repo.save(token);
        log.debug("Refresh token created for user: {} — expires: {}", user.getUsername(), saved.getExpiryDate());
        return saved;
    }

    // ── Verify ────────────────────────────────────────────────────────────────

    /**
     * Find and verify a refresh token by its UUID value.
     *
     * @return the RefreshToken if found and not expired
     * @throws TokenExpiredException if the token is past its expiry date
     * @throws TokenNotFoundException if the UUID doesn't exist in DB
     *         (could mean it was already rotated — possible replay attack)
     */
    public RefreshToken verifyToken(String tokenValue) {
        RefreshToken token = repo.findByToken(tokenValue)
                .orElseThrow(() -> {
                    log.warn("Refresh token not found — possible replay attack or already rotated: {}", tokenValue.substring(0, 8) + "...");
                    return new TokenNotFoundException("Refresh token not found or already used");
                });

        if (token.isExpired()) {
            // Token exists but has expired — delete it and force re-login
            repo.deleteByToken(tokenValue);
            log.info("Expired refresh token deleted for user: {}", token.getUser().getUsername());
            throw new TokenExpiredException("Refresh token expired — please login again");
        }

        return token;
    }

    // ── Rotate ────────────────────────────────────────────────────────────────

    /**
     * Rotate a refresh token: delete the old one, create a new one for the same user.
     * Called on every successful refresh to enforce single-use tokens.
     *
     * @param oldToken the token that was just used (will be deleted)
     * @return the newly created RefreshToken
     */
    public RefreshToken rotateToken(RefreshToken oldToken) {
        User user = oldToken.getUser();
        repo.deleteByToken(oldToken.getToken());
        log.debug("Rotated refresh token for user: {}", user.getUsername());
        return createToken(user);
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    /**
     * Delete a specific refresh token by its UUID value (single-session logout).
     */
    public void deleteToken(String tokenValue) {
        repo.findByToken(tokenValue).ifPresent(t -> {
            repo.deleteByToken(tokenValue);
            log.debug("Refresh token deleted for user: {}", t.getUser().getUsername());
        });
    }

    /**
     * Delete ALL refresh tokens for a user (logout from every device).
     */
    public void deleteAllTokensForUser(User user) {
        repo.deleteByUser(user);
        log.info("All refresh tokens deleted for user: {}", user.getUsername());
    }

    // ── Lookup ────────────────────────────────────────────────────────────────

    /** Find a token by value without throwing — used in the filter for silent checks. */
    public Optional<RefreshToken> findToken(String tokenValue) {
        return repo.findByToken(tokenValue);
    }

    // ── Exceptions ────────────────────────────────────────────────────────────

    public static class TokenExpiredException extends RuntimeException {
        public TokenExpiredException(String msg) { super(msg); }
    }

    public static class TokenNotFoundException extends RuntimeException {
        public TokenNotFoundException(String msg) { super(msg); }
    }
}
