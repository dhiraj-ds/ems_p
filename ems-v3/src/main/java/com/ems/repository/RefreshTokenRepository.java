package com.ems.repository;

import com.ems.entity.RefreshToken;
import com.ems.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /** Look up a token by its UUID value (used on every refresh attempt). */
    Optional<RefreshToken> findByToken(String token);

    /** All active tokens for a user — used for full logout (all devices). */
    List<RefreshToken> findByUser(User user);

    /** Delete a specific user's token — used on single-session logout. */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.token = :token")
    void deleteByToken(@Param("token") String token);

    /** Delete ALL tokens for a user — logout from every device. */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
    void deleteByUser(@Param("user") User user);

    /** Purge expired tokens — called on login to keep the table clean. */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < :now")
    void deleteAllExpired(@Param("now") Instant now);
}
