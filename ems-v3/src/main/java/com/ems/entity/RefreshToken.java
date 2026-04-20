package com.ems.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/**
 * Persisted refresh token — one row per active session per user.
 *
 * Design choices:
 *  - Token value is a random UUID (opaque, not JWT) — even if the DB leaks,
 *    the token is useless without the HttpOnly cookie binding.
 *  - expiryDate is checked on every refresh attempt; expired rows are rejected
 *    and deleted automatically.
 *  - Tokens are ROTATED on every use: old row deleted, new UUID inserted.
 *    This limits the replay window to effectively zero.
 *  - One user can have multiple rows (multiple devices/tabs); logout only
 *    deletes the token for the current session.
 */
@Entity
@Table(name = "refresh_tokens")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The opaque token value stored in the ems_refresh HttpOnly cookie.
     * Generated as UUID.randomUUID().toString() — 36 chars.
     */
    @Column(nullable = false, unique = true, length = 36)
    private String token;

    /**
     * The user this token belongs to.
     * No cascade — User lifecycle is independent.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Hard expiry — tokens are rejected after this point even if the cookie exists.
     * Default: 7 days from creation (set by RefreshTokenService).
     */
    @Column(nullable = false)
    private Instant expiryDate;

    /**
     * Audit — when was this token first issued (useful for security logs).
     */
    @Column(nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    /** Convenience — has this token passed its expiry time? */
    public boolean isExpired() {
        return Instant.now().isAfter(expiryDate);
    }
}
